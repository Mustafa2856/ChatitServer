package com.Chatit.Server.Tables;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="userchat")
public class UserChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private Timestamp timestamp;
    @ManyToOne
    @JoinColumn(name = "sent")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "received")
    private User receiver;
    @ManyToOne
    @JoinColumn(name = "tmsg")
    private Message message;

    public UserChat(User sender, User receiver, Message msg){
        this.sender = sender;
        this.receiver = receiver;
        this.message = msg;
    }

    public UserChat() {

    }

    public Long getId() {
        return id;
    }

    public Timestamp getTimeStamp(){return timestamp;}

    public User getReceiver() {
        return receiver;
    }

    public User getSender() {
        return sender;
    }

    public Message getMessage() {
        return message;
    }
}
