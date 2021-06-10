package com.Chatit.Server;

import com.Chatit.Server.Tables.Message;
import com.Chatit.Server.Tables.User;
import com.Chatit.Server.Tables.UserChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Transactional
@RestController
@SpringBootApplication
public class ServerApplication {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private MessageRepo msgRepo;
	@Autowired
	private UsrChatrepo usrchatrepo;

	@RequestMapping(value={"/","/error"})
	String HomeErrorPage(){
		return "This is a Spring REST API Backend for Chatit Application.No Web Pages are available here";
	}

	@RequestMapping(value = "/login",method = RequestMethod.POST)
	Long Login(HttpServletRequest request,String Email, String Password){
		List<User> chklist =  userRepo.findDistinctFirstByEmail(Email);
		for (User user : chklist) {
			if(user.validatePassword(Password)){
				request.getSession().setAttribute("user",user);
				return user.getid();
			}
		}
		return -1L;
	}

	@Transactional
	@RequestMapping(value="/register", method = RequestMethod.POST)
	Long Register(HttpServletRequest request,String Username,String Password,String Email){
		try{
			userRepo.save(new User(Username,Password,Email));
		}catch(Exception exp){
			exp.printStackTrace();
			return -1L;
		}
		List<User> chklist =  userRepo.findDistinctFirstByEmail(Email);
		for (User user : chklist) {
			if(user.validatePassword(Password)){
				request.getSession().setAttribute("user",user);
				return user.getid();
			}
		}
		return -1L;
	}

	@Transactional
	@RequestMapping(value="/changename", method = RequestMethod.POST)
	Long Changename(HttpServletRequest request,String Username,String Password,String Email){
		List<User> chklist =  userRepo.findDistinctFirstByEmail(Email);
		for (User user : chklist) {
			if(user.validatePassword(Password)){
				user.setUname(Username);
				userRepo.save(user);
				request.getSession().setAttribute("user",user);
				return user.getid();
			}
		}
		return -1L;
	}

	@RequestMapping(value="/chats")
	List<UserChat> getPendingChats(HttpServletRequest request,String Email,String Password){
		User currentUser = (User)request.getSession().getAttribute("user");
		if(currentUser == null) {
			Login(request,Email,Password);
			currentUser = (User)request.getSession().getAttribute("user");
			if(currentUser==null)return null;
		}
		List<UserChat> chats = usrchatrepo.findUserChatByReceiverOrderByTimestampDesc(currentUser);
		if(chats.size()>0)usrchatrepo.deleteUserChatByReceiver(currentUser);
		//if(chats.size()>0)usrchatrepo.deleteByReceiverAndTimeStamp(currentUser,chats.get(0).getTimeStamp());
		return chats;
	}

	@Transactional
	@RequestMapping(value="/message")
	Long message(HttpServletRequest request,String message,String ReceiverEmail,String Email,String Password){
		User currentUser = (User)request.getSession().getAttribute("user");
		if(currentUser==null)if(currentUser == null) {
			Login(request,Email,Password);
			currentUser = (User)request.getSession().getAttribute("user");
			if(currentUser==null)return null;
		}
		List<User> receiver = userRepo.findDistinctFirstByEmail(ReceiverEmail);
		if(receiver.size()==0)return 1L;
		if(message==null)return 3L;
		List<Message> msg = msgRepo.findDistinctByMessage(message);
		if(msg.size()==0){
			msgRepo.save(new Message(message));
			msg = msgRepo.findDistinctByMessage(message);
		}
		usrchatrepo.save(new UserChat(currentUser,receiver.get(0),msg.get(0)));
		return 2L;
	}

	@RequestMapping(value="/finduser")
	List<User> findUser(HttpServletRequest request,String uname){
		return userRepo.findByUname(uname);
	}

	public static void main(String[] args){
		SpringApplication.run(ServerApplication.class, args);
	}

}
