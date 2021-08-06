package com.Chatit.Server.Tables;

import javax.persistence.*;

/**
 * Message Table:
 *  Int     id
 *  String  message
 */
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "TEXT")
    private String message;

    @OneToOne(mappedBy = "message")
    private UserChat tmsg;

    public Message(String Message) {
        message = Message;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }
}