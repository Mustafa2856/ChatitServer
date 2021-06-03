package com.Chatit.Server.Tables;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String uname;
    private String email;
    private String password;

    public User(String Username,String Password,String Email){
        uname = Username;
        password = Password;
        email = Email;
    }

    public long getid(){
        return id;
    }

    public boolean validatePassword(String Password){
        return Password == this.password;
    }
}