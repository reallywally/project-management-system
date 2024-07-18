package com.wally.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long issueId;
    private String content;
}
