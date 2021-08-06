package com.Chatit.Server.Tables;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User Table:
 *  Int     id
 *  String  uname
 *  String  email
 *  String  password
 */
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String uname;
    @Column(unique = true,columnDefinition = "TEXT")
    private String email;
    @Column(columnDefinition = "TEXT")
    private String password;
    private byte[] publickey;

    @OneToMany(mappedBy = "sender")
    private Set<UserChat> sent = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<UserChat> received = new HashSet<>();

    public User(String Username,String Password,String Email,byte[] PublicKey){
        uname = Username;
        password = Password;
        email = Email;
        publickey = PublicKey;
    }

    public User() {

    }

    public String getEmail() { return email;}

    public String getUname() {
        return uname;
    }

    public byte[] getPublickey() {
        return publickey;
    }

    public void setPublickey(byte[] publickey){ this.publickey = publickey;}

    public void setUname(String uname) {
        this.uname = uname;
    }

    public boolean validatePassword(String Password){
        return (Password.equals(this.password));
    }
}