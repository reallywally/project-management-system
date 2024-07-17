package com.wally.service;

import com.wally.model.Issue;
import com.wally.model.Project;
import com.wally.model.User;
import com.wally.repository.IssueRepository;
import com.wally.request.IssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectService projectService;
    private final UserService userService;

    @Override
    public Issue getIssueById(Long id) throws Exception {

        Optional<Issue> issue = issueRepository.findById(id);

        if (issue.isPresent()) {
            return issue.get();
        }

        throw new Exception("Issue not found. ID: " + id);
    }

    @Override
    public List<Issue> getIssuesByProjectId(Long projectId) throws Exception {
        return issueRepository.findByProjectId(projectId);
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest, User user) throws Exception {

        Project project = projectService.getProjectById(issueRequest.getProjectId());

        Issue issue = new Issue();
        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setStatus(issueRequest.getStatus());
        issue.setProjectId(project.getId());
        issue.setPriority(issueRequest.getPriority());
        issue.setDueDate(issueRequest.getDueDate());

        issue.setProject(project);

        return issueRepository.save(issue);
    }

    @Override
    public void deleteIssue(Long issueId, Long userId) throws Exception {
        getIssueById(issueId);
        issueRepository.deleteById(issueId);
    }

    @Override
    public Issue addUserToIssue(Long issueId, Long userId) throws Exception {
        User user = userService.findUserById(userId);
        Issue issue = getIssueById(issueId);

        issue.setAssignee(user);

        return issueRepository.save(issue);
    }

    @Override
    public Issue updateStatus(Long issueId, String status) throws Exception {
        Issue issue = getIssueById(issueId);

        issue.setStatus(status);

        return issueRepository.save(issue);
    }
}
