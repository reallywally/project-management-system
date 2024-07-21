package com.wally.controller;

import com.wally.model.Chat;
import com.wally.model.Invitation;
import com.wally.model.Project;
import com.wally.model.User;
import com.wally.request.InviteRequest;
import com.wally.response.MessageResponse;
import com.wally.service.InvitationService;
import com.wally.service.ProjectService;
import com.wally.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final InvitationService invitationService;

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projectByTeam = projectService.getProjectByTeam(user, category, tag);

        return new ResponseEntity<>(projectByTeam, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Project> getProjectsById(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Project project = projectService.getProjectById(projectId);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(
            @RequestBody Project project,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Project createdProject = projectService.createProject(project, user);

        return new ResponseEntity<>(createdProject, HttpStatus.OK);
    }

    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long projectId,
            @RequestBody Project project,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Project updatedProject = projectService.updateProject(project, projectId);

        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }


    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<MessageResponse> deleteProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        projectService.deleteProject(projectId, user.getId());

        MessageResponse messageResponse = new MessageResponse("Project deleted successfully");

        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/project/search")
    public ResponseEntity<List<Project>> searchProject(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projects = projectService.searchProject(keyword, user);

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/chat")
    public ResponseEntity<Chat> getChatByProjectId(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Chat chat = projectService.getChatByProjectId(projectId);

        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<MessageResponse> inviteProject(
            @RequestBody InviteRequest inviteRequest,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        invitationService.sendInvitation(inviteRequest.getEmail(), inviteRequest.getProjectId());

        MessageResponse messageResponse = new MessageResponse("Project deleted successfully");

        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/accept-invitation")
    public ResponseEntity<Invitation> acceptInviteProject(
            @RequestParam String token,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Invitation invitation = invitationService.acceptInvitation(token, user.getId());
        projectService.addUserToProject(invitation.getProjectId(), user.getId());

        return new ResponseEntity<>(invitation, HttpStatus.ACCEPTED);
    }
}
