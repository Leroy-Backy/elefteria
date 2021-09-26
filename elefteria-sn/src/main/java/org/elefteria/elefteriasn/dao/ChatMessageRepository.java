package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select chm from ChatMessage chm order by chm.createdDate desc")
    Page<ChatMessage> getAllOrderByCreatedDateDesc(Pageable pageable);

    @Query("select chm from ChatMessage chm where chm.id > ?1 order by chm.createdDate desc")
    List<ChatMessage> getMessagesAfter(Long afterId);
}
