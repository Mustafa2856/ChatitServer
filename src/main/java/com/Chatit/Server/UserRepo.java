package com.Chatit.Server;

import com.Chatit.Server.Tables.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findByUname(String Uname);
}