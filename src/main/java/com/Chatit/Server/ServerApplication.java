package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
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
    String HomeErrorPage() {
        return "This is a Spring REST API Backend for Chatit Application.No Web Pages are available here";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    Long Login(HttpServletRequest request, String Email, String Password) {
        List<User> chklist = userRepo.findDistinctFirstByEmail(Email);
        for (User user : chklist) {
            if (user.validatePassword(Password)) {
                request.getSession().setAttribute("user", user);
                return user.getid();
            }
        }
        return -1L;
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    Long Register(HttpServletRequest request, String Username, String Password, String Email, String PublicKey) {
        try {
            userRepo.save(new User(Username, Password, Email, hexStringToByteArray(PublicKey)));
        } catch (Exception exp) {
            exp.printStackTrace();
            return -1L;
        }
        List<User> chklist = userRepo.findDistinctFirstByEmail(Email);
        for (User user : chklist) {
            if (user.validatePassword(Password)) {
                request.getSession().setAttribute("user", user);
                return user.getid();
            }
        }
        return -1L;
    }

    @Transactional
    @RequestMapping(value = "/changename", method = RequestMethod.POST)
    Long Changename(HttpServletRequest request, String Username, String Password, String Email) {
        List<User> chklist = userRepo.findDistinctFirstByEmail(Email);
        for (User user : chklist) {
            if (user.validatePassword(Password)) {
                user.setUname(Username);
                userRepo.save(user);
                request.getSession().setAttribute("user", user);
                return user.getid();
            }
        }
        return -1L;
    }

    @RequestMapping(value = "/chats")
    List<UserChat> getPendingChats(HttpServletRequest request, String Email, String Password) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            Login(request, Email, Password);
            currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) return null;
        }
        List<UserChat> chats = usrchatrepo.findUserChatByReceiverOrderByTimestampDesc(currentUser);
        for (UserChat chat : chats) {
            usrchatrepo.delete(chat);
            msgRepo.delete(chat.getMessage());
        }
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
    @RequestMapping(value = "/message")
    Long message(HttpServletRequest request, String message, String ReceiverEmail, String Email, String Password) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            Login(request, Email, Password);
            currentUser = (User) request.getSession().getAttribute("user");
            if (currentUser == null) return null;
        }
        List<User> receiver = userRepo.findDistinctFirstByEmail(ReceiverEmail);
        if (receiver.size() == 0) return 1L;
        if (message == null) return 3L;
        Message msg = new Message(message);
        msgRepo.save(msg);
        usrchatrepo.save(new UserChat(currentUser, receiver.get(0), msg));
        return 2L;
    }

    @RequestMapping(value = "/finduser")
    List<User> findUser(String uname) {
        return userRepo.findByUnameContains(uname);
    }

}
