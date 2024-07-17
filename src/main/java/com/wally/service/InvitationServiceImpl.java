package com.wally.service;

import com.wally.model.Invitation;
import com.wally.repository.InvitationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EmailService emailService;

    @Override
    public void sendInvitation(String userEmail, Long projectId) throws MessagingException {

        String invitationToken = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();
        invitation.setEmail(userEmail);
        invitation.setProjectId(projectId);
        invitation.setToken(invitationToken);

        invitationRepository.save(invitation);

        String invitationLink = "http://localhost:8080/accept_invitaion?token" + invitationToken;
        emailService.sendEmail(userEmail, invitationLink);
    }

    @Override
    public Invitation acceptInvitation(String token, Long userId) throws Exception {

        Invitation invitation = invitationRepository.findByToken(token);

        if (invitation == null) {
            throw new Exception("초대장이 존재하지 않습니다.");
        }

        return invitation;
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {

        Invitation invitation = invitationRepository.findByEmail(userEmail);

        return invitation.getToken();
    }

    @Override
    public void deleteInvitation(String token) {

        Invitation invitation = invitationRepository.findByToken(token);

        invitationRepository.delete(invitation);
    }
}
