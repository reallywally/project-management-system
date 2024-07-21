package com.wally.controller;

import com.wally.model.Message;
import com.wally.model.Project;
import com.wally.model.User;
import com.wally.request.CreateMessageRequest;
import com.wally.service.MessageService;
import com.wally.service.ProjectService;
import com.wally.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping("/messages/send")
    public ResponseEntity<Message> sendMessage(
            @RequestBody CreateMessageRequest createMessageRequest) throws Exception {

        //이런 체크는 service에서 하는게 맞는거 같은데
        User user = userService.findUserById(createMessageRequest.getSenderId());
        if (user == null) {
            throw new Exception("User not found. sender Id: " + createMessageRequest.getSenderId());
        }

        Project project = projectService.getProjectById(createMessageRequest.getProjectId());
        if (project == null) {
            throw new Exception("Project not found. project Id: " + createMessageRequest.getProjectId());
        }

        Message sendMessage = messageService.sendMessage(createMessageRequest.getSenderId(), createMessageRequest.getProjectId(), createMessageRequest.getContent());

        return ResponseEntity.ok(sendMessage);
    }

    @GetMapping("/messages/chat/{projectId}")
    public ResponseEntity<List<Message>> getMessagesByChatId(
            @PathVariable Long projectId) throws Exception {

        List<Message> messages = messageService.getMessageByProjectId(projectId);

        return ResponseEntity.ok(messages);
    }
}
