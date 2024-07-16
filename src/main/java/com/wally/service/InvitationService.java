package com.wally.service;

import com.wally.model.Invitation;

public interface InvitationService {

    public void sendInvitation(String userEmail, Long projectId);

    public Invitation acceptInvitation(String token, Long userId);

    public String getTokenByUserEmail(String userEmail);

    public void deleteInvitation(String token);
}
