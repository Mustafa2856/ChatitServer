package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
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

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @RequestMapping(value = {"/", "/error"})
    String homeErrorPage() {
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
            userRepo.save(new User(Username, Password, Email, hexStringToByteArray(PublicKey)));
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
        return Login(Email, Password);
    }

    @Transactional
    @RequestMapping(value = "/setpkey", method = RequestMethod.POST)
    Long SetPkey(String Email, String Password, String PublicKey){
        User user = Login(Email, Password);
        if(user != null){
            user.setPublickey(hexStringToByteArray(PublicKey));
            userRepo.save(user);
            return 0L;
        }
        return -1L;
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

    @RequestMapping(value = "/chats", method = RequestMethod.POST)
    List<UserChat> getPendingChats(String Email, String Password, String Timestamp) {
        User currentUser = Login(Email, Password);
        List<UserChat> chats = usrchatrepo.findUserChatByReceiverAndTimestampAfter(currentUser, java.sql.Timestamp.valueOf(Timestamp));
        return chats;
    }

    private String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    @RequestMapping(value = "/getpkey")
    String getPublicKey(String email) {
        try {
            return toHexString(userRepo.findDistinctFirstByEmail(email).get(0).getPublickey());
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    Long message(String message, String ReceiverEmail, String Email, String Password) {
        User currentUser = Login(Email, Password);
        List<User> receiver = userRepo.findDistinctFirstByEmail(ReceiverEmail);
        if (receiver.size() == 0) return 1L;
        if (message == null) return 3L;
        Message msg = new Message(message);
        msgRepo.save(msg);
        usrchatrepo.save(new UserChat(currentUser, receiver.get(0), msg));
        return 2L;
    }

    @RequestMapping(value = "/finduser")
    List<User> findUser(String email) {
        List<User> res = userRepo.findByEmailContains(email);
        int limit = 10;
        if(res.size() > limit){
            res = res.subList(0,limit);
        }
        return res;
    }

}
