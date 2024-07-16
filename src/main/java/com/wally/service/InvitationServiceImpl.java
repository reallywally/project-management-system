package com.wally.service;

import com.wally.model.Invitation;

public class InvitationServiceImpl implements InvitationService{
    @Override
    public void sendInvitation(String userEmail, Long projectId) {

    }

    @Override
    public Invitation acceptInvitation(String token, Long userId) {
        return null;
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        return null;
    }

    @Override
    public void deleteInvitation(String token) {

    }
}
