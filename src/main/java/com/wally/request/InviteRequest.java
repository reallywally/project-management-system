package com.wally.request;

import lombok.Data;

@Data
public class InviteRequest {
    private Long projectId;
    private String email;
}
