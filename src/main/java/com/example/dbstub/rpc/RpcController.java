package com.example.dbstub.rpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rpc")
@RequiredArgsConstructor
@Slf4j
public class RpcController {

    private final RpcService rpcService;

    @PostMapping("/process")
    public RpcResponse process(@RequestBody RpcRequest request) {
        log.info("RPC REST call: header={}, requestId={}", 
            request.getHeader(), request.getRequestId());
        return rpcService.processRequest(request);
    }

    @GetMapping("/health")
    public String health() {
        return "RPC Service is running";
    }
}
