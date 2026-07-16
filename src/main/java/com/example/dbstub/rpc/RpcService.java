package com.example.dbstub.rpc;

import com.example.dbstub.dto.KafkaMessage;
import com.example.dbstub.kafka.KafkaProducer;
import com.example.dbstub.service.AiService;
import com.example.dbstub.service.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpcService {

    private final KafkaProducer kafkaProducer;
    private final AiService aiService;
    private final DatabaseService databaseService;
    private final ObjectMapper objectMapper;
    private final Map<String, RpcResponse> pendingRequests = new ConcurrentHashMap<>();
    private static final long TIMEOUT_SECONDS = 30;

    public RpcResponse processRequest(RpcRequest request) {
        String header = request.getHeader();
        String requestId = request.getRequestId() != null ? 
            request.getRequestId() : UUID.randomUUID().toString();
        
        log.info("RPC request received. Header: {}, RequestId: {}", header, requestId);

        if (!isValidHeader(header)) {
            log.warn("Unknown header: {}", header);
            return RpcResponse.builder()
                .requestId(requestId)
                .header(header)
                .status("ERROR")
                .errorMessage("Unknown header: " + header + ". Available: PROCESS_TEXT, GET_HISTORY, GET_BY_ID")
                .build();
        }

        try {
            String result;
            String status = "SUCCESS";
            String errorMessage = null;

            switch (header) {
                case "PROCESS_TEXT":
                    result = aiService.processWithAI(request.getPayload());
                    var dbRequest = databaseService.saveText(request.getPayload());
                    databaseService.updateResponse(dbRequest.getId(), result);
                    
                    KafkaMessage kafkaMessage = new KafkaMessage();
                    kafkaMessage.setId(requestId);
                    kafkaMessage.setText(request.getPayload());
                    kafkaMessage.setStatus("SUCCESS");
                    kafkaProducer.sendSuccess(kafkaMessage);
                    
                    log.info("Saved to DB with ID: {}", dbRequest.getId());
                    break;
                    
                case "GET_HISTORY":
                    result = "History data";
                    break;
                    
                case "GET_BY_ID":
                    Long id = Long.parseLong(request.getPayload());
                    var requestEntity = databaseService.getRequestById(id);
                    result = String.format(
                        "Request ID: %d\nTask: %s\nResponse: %s",
                        id,
                        requestEntity.getTask().getQuestion(),
                        requestEntity.getResponse().getAnswer()
                    );
                    break;
                    
                default:
                    status = "ERROR";
                    errorMessage = "Unknown header: " + header;
                    result = null;
            }

            if (status.equals("ERROR")) {
                return RpcResponse.builder()
                    .requestId(requestId)
                    .header(header)
                    .status(status)
                    .errorMessage(errorMessage)
                    .build();
            }

            return RpcResponse.builder()
                .requestId(requestId)
                .header(header)
                .status("SUCCESS")
                .payload(result)
                .build();

        } catch (Exception e) {
            log.error("Error processing RPC request: {}", e.getMessage(), e);
            return RpcResponse.builder()
                .requestId(requestId)
                .header(header)
                .status("ERROR")
                .errorMessage("Internal error: " + e.getMessage())
                .build();
        }
    }

    private boolean isValidHeader(String header) {
        return header != null && !header.isEmpty() && 
            (header.equals("PROCESS_TEXT") || 
             header.equals("GET_HISTORY") ||
             header.equals("GET_BY_ID"));
    }
}
