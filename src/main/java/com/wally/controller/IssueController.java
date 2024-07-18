package com.wally.controller;

import com.wally.model.Issue;
import com.wally.model.IssueDto;
import com.wally.model.User;
import com.wally.request.IssueRequest;
import com.wally.response.MessageResponse;
import com.wally.service.IssueService;
import com.wally.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;
    private final UserService userService;

    @GetMapping("/issues/{issueId}")
    public ResponseEntity<Issue> getIssueById(
            @PathVariable Long issueId) throws Exception {

        Issue issue = issueService.getIssueById(issueId);

        return ResponseEntity.ok(issue);
    }

    @GetMapping("/issues/project/{projectId}")
    public ResponseEntity<List<Issue>> getIssuesByProjectId(
            @PathVariable Long projectId) throws Exception {

        List<Issue> issues = issueService.getIssuesByProjectId(projectId);

        return ResponseEntity.ok(issues);
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueDto> createIssue(
            @RequestBody IssueRequest issueRequest,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Issue issue = issueService.createIssue(issueRequest, user);

        IssueDto issueDto = new IssueDto();
        issueDto.setId(issue.getId());
        issueDto.setTitle(issue.getTitle());
        issueDto.setDescription(issue.getDescription());
        issueDto.setPriority(issue.getPriority());
        issueDto.setStatus(issue.getStatus());
        issueDto.setDueDate(issue.getDueDate());
        issueDto.setProjectId(issue.getProjectId());


        return ResponseEntity.ok(issueDto);
    }

    @DeleteMapping("/issues/{issueId}")
    public ResponseEntity<MessageResponse> deleteIssue(
            @PathVariable Long issueId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        issueService.deleteIssue(issueId, user.getId());

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Issue deleted successfully");

        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/issues/{issueId}/assignee/{userId}")
    public ResponseEntity<Issue> addUserToIssue(
            @PathVariable Long issueId,
            @PathVariable Long userId) throws Exception {

        Issue issue = issueService.addUserToIssue(issueId, userId);

        return ResponseEntity.ok(issue);
    }

    @PutMapping("/issues/{issueId}/status/{status")
    public ResponseEntity<Issue> updateIssueStatus(
            @PathVariable Long issueId,
            @PathVariable String status) throws Exception {

        Issue issue = issueService.updateStatus(issueId, status);

        return ResponseEntity.ok(issue);
    }

}
