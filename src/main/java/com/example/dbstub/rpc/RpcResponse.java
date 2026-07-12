package com.example.dbstub.rpc;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {
    private String requestId;
    private String header;
    private String payload;
    private String status;
    private String errorMessage;
}
