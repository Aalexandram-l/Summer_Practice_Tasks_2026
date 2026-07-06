package com.example.dbstub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage {
    private String id;
    private String text;
    private String status;
    private String error;
}
