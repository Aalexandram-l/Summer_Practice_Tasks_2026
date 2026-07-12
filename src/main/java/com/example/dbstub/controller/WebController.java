package com.example.dbstub.controller;

import com.example.dbstub.rpc.RpcRequest;
import com.example.dbstub.rpc.RpcResponse;
import com.example.dbstub.rpc.RpcService;
import com.example.dbstub.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final RpcService rpcService;
    private final DatabaseService databaseService;

    @GetMapping("/")
    public String index() {
        log.info("Index page requested");
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model) {
        var requests = databaseService.getAllRequests();
        model.addAttribute("requests", requests);
        return "history";
    }

    @PostMapping("/process")
    @ResponseBody
    public RpcResponse process(@RequestParam String text) {
        log.info("Processing text: {}", text);
        
        RpcRequest request = new RpcRequest();
        request.setRequestId(java.util.UUID.randomUUID().toString());
        request.setHeader("PROCESS_TEXT");
        request.setPayload(text);
        
        return rpcService.processRequest(request);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public RpcResponse getById(@PathVariable String id) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(java.util.UUID.randomUUID().toString());
        request.setHeader("GET_BY_ID");
        request.setPayload(id);
        
        return rpcService.processRequest(request);
    }
}
