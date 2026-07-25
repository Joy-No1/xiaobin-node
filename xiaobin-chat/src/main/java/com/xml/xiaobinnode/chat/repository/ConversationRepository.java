package com.xml.xiaobinnode.chat.repository;

import com.xml.xiaobinnode.chat.document.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    List<Conversation> findByUser1IdOrUser2IdOrderByUpdatedAtDesc(Long user1Id, Long user2Id);
}
