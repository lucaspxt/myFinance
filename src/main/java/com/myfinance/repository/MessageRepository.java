package com.myfinance.repository;

import com.myfinance.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByMemoryId(Long memoryId);
}
