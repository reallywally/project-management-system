package com.wally.controller;

import com.wally.model.Comment;
import com.wally.model.User;
import com.wally.request.CommentRequest;
import com.wally.response.MessageResponse;
import com.wally.service.CommentService;
import com.wally.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(
            @RequestBody CommentRequest commentRequest,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Comment comment = commentService.createComment(commentRequest.getIssueId(), user.getId(), commentRequest.getContent());

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        commentService.deleteComment(commentId, user.getId());

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Issue deleted successfully");

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/comments/{issueId}")
    public ResponseEntity<List<Comment>> getCommentsByIssueId(
            @PathVariable Long issueId) throws Exception {

        List<Comment> comments = commentService.findCommentsByIssueId(issueId);

        return ResponseEntity.ok(comments);
    }
}
