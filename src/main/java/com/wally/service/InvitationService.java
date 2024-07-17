package com.wally.service;

import com.wally.model.Invitation;
import jakarta.mail.MessagingException;

public interface InvitationService {

    public void sendInvitation(String userEmail, Long projectId) throws MessagingException;

    public Invitation acceptInvitation(String token, Long userId) throws Exception;

    public String getTokenByUserEmail(String userEmail);

    public void deleteInvitation(String token);
}
