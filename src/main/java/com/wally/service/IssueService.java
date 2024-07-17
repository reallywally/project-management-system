package com.wally.service;

import com.wally.model.Issue;
import com.wally.model.User;
import com.wally.request.IssueRequest;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    Issue getIssueById(Long id) throws Exception;

    List<Issue> getIssuesByProjectId(Long projectId) throws Exception;

    Issue createIssue(IssueRequest issueRequest, User user) throws Exception;

    // Optional<Issue> updateIssue(Long issueId, IssueRequest issueRequest, Long userId);

    void deleteIssue(Long issueId, Long userId) throws Exception;

    // List<Issue> getIssuesBuAssigneeId(Long assigneeId);

    // List<Issue> searchIssues(String title, String sttus, String priority, Long assigneeId);

    // List<User> getAssigneeForIssue(Long issueId) throws Exception;

    Issue addUserToIssue(Long issueId, Long userId) throws Exception;

    Issue updateStatus(Long issueId, String status) throws Exception;

}
