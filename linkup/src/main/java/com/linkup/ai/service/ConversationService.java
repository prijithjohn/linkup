package com.linkup.ai.service;

import com.linkup.ai.dto.LinkedInRequest;
import com.linkup.ai.dto.LinkedInResponse;
import com.linkup.ai.entity.User;
import com.linkup.ai.model.Conversation;
import com.linkup.ai.repository.ConversationRepository;
import com.linkup.ai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository, UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        String username = auth.getName();
        if (username == null) return Optional.empty();
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Optional<Conversation> saveConversation(LinkedInRequest request, LinkedInResponse response) {
        try {
            Optional<User> maybeUser = getAuthenticatedUser();
            if (maybeUser.isEmpty()) return Optional.empty();

            User user = maybeUser.get();
            Conversation c = new Conversation();
            c.setMessageContent(request == null ? null : request.getMessageContent());
            c.setReply(response == null ? null : response.getReply());
            c.setTone(request == null ? null : request.getTone());
            c.setAction(request == null ? null : request.getAction());
            c.setRecipientName(request == null ? null : request.getRecipientName());
            c.setTargetRole(request == null ? null : request.getTargetRole());
            c.setTargetCompany(request == null ? null : request.getTargetCompany());
            c.setUser(user);

            Conversation saved = conversationRepository.save(c);
            return Optional.of(saved);
        } catch (Exception e) {
            logger.warn("Failed to save conversation: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Conversation> listForCurrentUser() {
        Optional<User> maybeUser = getAuthenticatedUser();
        if (maybeUser.isEmpty()) return List.of();
        return conversationRepository.findByUserId(maybeUser.get().getId());
    }

    @Transactional(readOnly = true)
    public Optional<Conversation> findByIdForCurrentUser(Long id) {
        Optional<User> maybeUser = getAuthenticatedUser();
        if (maybeUser.isEmpty()) return Optional.empty();
        return conversationRepository.findByIdAndUserId(id, maybeUser.get().getId());
    }

    @Transactional
    public boolean deleteByIdForCurrentUser(Long id) {
        Optional<User> maybeUser = getAuthenticatedUser();
        if (maybeUser.isEmpty()) return false;
        try {
            conversationRepository.deleteByIdAndUserId(id, maybeUser.get().getId());
            return true;
        } catch (Exception e) {
            logger.warn("Failed to delete conversation {}: {}", id, e.getMessage());
            return false;
        }
    }
}
