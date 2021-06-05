package com.Chatit.Server.Tables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String message;

    @OneToMany(mappedBy = "message")
    private Set<Message> tmsg = new HashSet<>();

    public Message(String Message){
        message = Message;
    }

    public Message() {

    }

    public long getid(){
        return id;
    }

    public String getMessage() {
        return message;
    }
}