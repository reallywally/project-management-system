package com.wally.request;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProjectUpdate {
    private String name;
    private String description;
    private String category;
    private List<String> tags = new ArrayList<>();
    private Long ownerId;
}
