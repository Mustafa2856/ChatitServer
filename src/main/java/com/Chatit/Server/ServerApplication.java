package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
    User Register(@RequestBody Map<String,Object> data) {
        String Username = data.get("Username").toString();
        String Password = data.get("Password").toString();
        String Email = data.get("Email").toString();
        String PublicKey = data.get("PublicKey").toString();
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
    Long setPublicKey(@RequestBody Map<String,Object> data){
        String Password = data.get("Password").toString();
        String Email = data.get("Email").toString();
        String PublicKey = data.get("PublicKey").toString();
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
    Long changeName(@RequestBody Map<String,Object> data) {
        String Username = data.get("Username").toString();
        String Password = data.get("Password").toString();
        String Email = data.get("Email").toString();
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
        String s = new String(data);
        System.out.println(s);
        int index = 0,i;
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
        bout.write(data,i+1, data.length - i-1);
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
    @ResponseBody byte[] getPendingChats(@RequestBody Map<String,Object> data) throws IOException {
        String Timestamp = data.get("Timestamp").toString();
        String Password = data.get("Password").toString();
        String Email = data.get("Email").toString();
        User currentUser = Login(Email, Password);
        UserChat chat = usrchatrepo.findFirstByReceiverAndTimestampAfterOrderByTimestampAsc(currentUser, java.sql.Timestamp.valueOf(Timestamp)).get(0);
        String Sender_uname = chat.getSender().getUname();
        String Sender_email = chat.getSender().getEmail();
        String ts = chat.getTimeStamp().toString();
        String type = chat.getMessage().getType().toString();
        byte[] msg = chat.getMessage().getMessage();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(Base64.getEncoder().encode(Sender_email.getBytes(StandardCharsets.UTF_8)));
        bout.write('-');
        bout.write(Base64.getEncoder().encode(Sender_uname.getBytes(StandardCharsets.UTF_8)));
        bout.write('-');
        bout.write(Base64.getEncoder().encode(ts.getBytes(StandardCharsets.UTF_8)));
        bout.write('-');
        bout.write(Base64.getEncoder().encode(type.getBytes(StandardCharsets.UTF_8)));
        bout.write('-');
        bout.write(msg);
        return bout.toByteArray();
    }
}
