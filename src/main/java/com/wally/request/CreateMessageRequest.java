package com.wally.request;

import lombok.Data;

@Data
public class CreateMessageRequest {
    private Long senderId;
    private String content;
    private Long projectId;
}
