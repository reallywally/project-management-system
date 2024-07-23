package com.wally.service;

import com.wally.model.Comment;
import com.wally.model.Issue;
import com.wally.model.User;
import com.wally.repository.CommentRepository;
import com.wally.repository.IssueRepository;
import com.wally.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Override
    public Comment createComment(Long issueId, Long userId, String content) throws Exception {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        Optional<User> userOptional = userRepository.findById(userId);

        if(issueOptional.isEmpty()){
            throw new Exception("Issue not found. ID: " + issueId);
        }
        if(userOptional.isEmpty()){
            throw new Exception("User not found. ID: " + userId);
        }

        Comment comment = new Comment();
        comment.setIssue(issueOptional.get());
        comment.setUser(userOptional.get());
        comment.setContent(content);
        comment.setCreatedDateTime(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        // issueOptional.get().getComments().add(savedComment);

        return savedComment;


    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws Exception {

        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        Optional<User> userOptional = userRepository.findById(userId);

        if(commentOptional.isEmpty()){
            throw new Exception("Comment not found. ID: " + commentId);
        }
        if(userOptional.isEmpty()){
            throw new Exception("User not found. ID: " + userId);
        }

        Comment comment = commentOptional.get();
        User user = userOptional.get();

        if(comment.getUser().equals(user)){
            commentRepository.deleteById(commentId);
        }else{
            throw new Exception("User is not authorized to delete this comment.");
        }
    }

    @Override
    public List<Comment> findCommentsByIssueId(Long issueId) throws Exception {
        return commentRepository.findCommentByIssueId(issueId);
    }
}
