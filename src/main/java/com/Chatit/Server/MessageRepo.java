package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findDistinctByMessage(String Message);
}
