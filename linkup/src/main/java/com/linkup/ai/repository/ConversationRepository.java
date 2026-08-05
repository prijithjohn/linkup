package com.linkup.ai.repository;

import com.linkup.ai.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
	List<Conversation> findByUserId(Long userId);

	Optional<Conversation> findByIdAndUserId(Long id, Long userId);

	void deleteByIdAndUserId(Long id, Long userId);
}
