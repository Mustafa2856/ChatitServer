package com.Chatit.Server.Tables;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;

/**
 * Message Table:
 *  Int     id
 *  String  message
 */
@Entity
@Table(name = "message")
public class Message {

    public Message() {

    }

    public enum MSGTYPE{TEXT,IMG,AUDIO,VIDEO}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private byte[] message;

    private MSGTYPE type;

    @OneToOne(mappedBy = "message")
    private UserChat tmsg;

    public Message(byte[] Message,MSGTYPE Type){
        message = Message;
        type = Type;
    }

    public byte[] getMessage() {
        return message;
    }

    public MSGTYPE getType(){
        return type;
    }
}