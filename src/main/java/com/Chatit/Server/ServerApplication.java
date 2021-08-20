package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;

/**
 * REST API to provide server functions to the android chatting application Chatit
 */
@Transactional
@RestController
@SpringBootApplication
public class ServerApplication {

    private final UserRepo userRepo;
    private final MessageRepo msgRepo;
    private final UsrChatrepo usrchatrepo;

    public ServerApplication(UserRepo userRepo, MessageRepo msgRepo, UsrChatrepo usrchatrepo) {
        this.userRepo = userRepo;
        this.msgRepo = msgRepo;
        this.usrchatrepo = usrchatrepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @RequestMapping(value = {"/"})
    String homePage() {
        return "This is a Spring REST API Backend for Chatit Application.No Web Pages are available here";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    User Login(String Email, String Password) {
        List<User> chklist = userRepo.findDistinctFirstByEmail(Email);
        for (User user : chklist) {
            if (user.validatePassword(Password)) {
                return user;
            }
        }
        return null;
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    User Register(String Username, String Password, String Email, String PublicKey) {
        try {
            userRepo.save(new User(Username, Password, Email, Base64.getDecoder().decode(PublicKey)));
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
        return Login(Email, Password);
    }

    @Transactional
    @RequestMapping(value = "/setpkey", method = RequestMethod.POST)
    Long setPublicKey(String Email, String Password, String PublicKey){
        User user = Login(Email, Password);
        if(user != null){
            user.setPublickey(Base64.getDecoder().decode(PublicKey));
            userRepo.save(user);
            return 0L;
        }
        return -1L;
    }

    @RequestMapping(value = "/getpkey")
    String getPublicKey(String Email) {
        try {
            return Base64.getEncoder().encodeToString(userRepo.findDistinctFirstByEmail(Email).get(0).getPublickey());
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    @RequestMapping(value = "/changename", method = RequestMethod.POST)
    Long changeName(String Username, String Password, String Email) {
        User user = Login(Email, Password);
        if(user != null){
            user.setUname(Username);
            userRepo.save(user);
            return 0L;
        }
        return -1L;
    }

    @RequestMapping(value = "/finduser")
    List<User> findUser(String Email) {
        List<User> res = userRepo.findByEmailContains(Email);
        int limit = 10;
        if(res.size() > limit){
            res = res.subList(0,limit);
        }
        return res;
    }

    @Transactional
    @RequestMapping(value = "/message",method = RequestMethod.POST)
    Long message(@RequestBody byte[] data){
        String Email = null,Password = null,ReceiverEmail = null,Type = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int index = 0,i=0;
        for(i=0;i<data.length;i++){
            if(data[i]== '-') {
                if(index == 0){
                    Email = new String(Base64.getDecoder().decode(bout.toByteArray()));
                    bout = new ByteArrayOutputStream();
                }
                else if(index == 1){
                    Password = new String(Base64.getDecoder().decode(bout.toByteArray()));
                    bout = new ByteArrayOutputStream();
                }
                else if(index == 2){
                    ReceiverEmail = new String(Base64.getDecoder().decode(bout.toByteArray()));
                    bout = new ByteArrayOutputStream();
                }
                else{
                    Type = new String(Base64.getDecoder().decode(bout.toByteArray()));
                    break;
                }
                index ++;
            }
            else bout.write(data[i]);
        }
        bout = new ByteArrayOutputStream();
        User currentUser = Login(Email, Password);
        List<User> receiver = userRepo.findDistinctFirstByEmail(ReceiverEmail);
        if (receiver.size() == 0 || currentUser == null) return 1L;
        bout.write(data,i, data.length - i);
        byte[] msgBytes = bout.toByteArray();
        try{
            Message.MSGTYPE type = Message.MSGTYPE.valueOf(Type);
            Message msg = new Message(msgBytes,type);
            msgRepo.save(msg);
            usrchatrepo.save(new UserChat(currentUser,receiver.get(0),msg));
            return 2L;
        }
        catch(IllegalArgumentException exp){
            exp.printStackTrace();
        }
        return 0L;
    }

    @RequestMapping(value = "/chats", method = RequestMethod.POST)
    List<UserChat> getPendingChats(String Email, String Password, String Timestamp) {
        User currentUser = Login(Email, Password);
        List<UserChat> chats = usrchatrepo.findFirstByReceiverAndTimestampAfter(currentUser, java.sql.Timestamp.valueOf(Timestamp));
        return chats;
    }

}
