package com.pms.dto.response;

import com.pms.entity.Attachment;

import java.time.LocalDateTime;

public class AttachmentResponse {
    
    private Long id;
    private String originalName;
    private String storedName;
    private String contentType;
    private Long fileSize;
    private String formattedFileSize;
    private Boolean isImage;
    private String downloadUrl;
    private Long issueId;
    private UserResponse uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public AttachmentResponse() {}
    
    // Static factory method
    public static AttachmentResponse from(Attachment attachment) {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachment.getId());
        response.setOriginalName(attachment.getOriginalName());
        response.setStoredName(attachment.getStoredName());
        response.setContentType(attachment.getContentType());
        response.setFileSize(attachment.getFileSize());
        response.setFormattedFileSize(attachment.getFormattedFileSize());
        response.setIsImage(attachment.isImage());
        response.setDownloadUrl("/api/v1/attachments/" + attachment.getId() + "/download");
        response.setIssueId(attachment.getIssue().getId());
        response.setUploadedBy(UserResponse.basicInfo(attachment.getUploadedBy()));
        response.setCreatedAt(attachment.getCreatedAt());
        response.setUpdatedAt(attachment.getUpdatedAt());
        
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getStoredName() {
        return storedName;
    }
    
    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFormattedFileSize() {
        return formattedFileSize;
    }
    
    public void setFormattedFileSize(String formattedFileSize) {
        this.formattedFileSize = formattedFileSize;
    }
    
    public Boolean getIsImage() {
        return isImage;
    }
    
    public void setIsImage(Boolean isImage) {
        this.isImage = isImage;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public Long getIssueId() {
        return issueId;
    }
    
    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }
    
    public UserResponse getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(UserResponse uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 