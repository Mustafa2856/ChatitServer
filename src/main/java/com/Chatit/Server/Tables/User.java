package com.Chatit.Server.Tables;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String uname;
    @Column(unique = true)
    private String email;
    private String password;

    @OneToMany(mappedBy = "sender")
    private Set<UserChat> sent = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<UserChat> received = new HashSet<>();

    public User(String Username,String Password,String Email){
        uname = Username;
        password = Password;
        email = Email;
    }

    public User() {

    }

    public long getid(){
        return id;
    }

    public String getEmail() { return email;}

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public boolean validatePassword(String Password){
        return (Password.equals(this.password));
    }
}