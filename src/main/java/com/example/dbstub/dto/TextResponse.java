package com.example.dbstub.dto;

public class TextResponse {
    private Long requestId;
    private String question;
    private String answer;
    private String status;

    public TextResponse(Long requestId, String question, String answer, String status) {
        this.requestId = requestId;
        this.question = question;
        this.answer = answer;
        this.status = status;
    }

    public Long getRequestId() { return requestId; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getStatus() { return status; }
}
