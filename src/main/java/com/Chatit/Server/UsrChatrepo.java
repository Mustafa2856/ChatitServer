package com.Chatit.Server;

import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Transactional
public interface UsrChatrepo extends JpaRepository<UserChat, Long> {
    List<UserChat> findFirstByReceiverAndTimestampAfterOrderByTimestampAsc(User Reciever,Timestamp timestamp);
}
