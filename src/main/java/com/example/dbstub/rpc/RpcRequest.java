package com.example.dbstub.rpc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {
    private String header;
    private String payload;
    private String requestId;
}
