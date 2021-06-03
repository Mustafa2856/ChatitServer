package com.Chatit.Server;

import com.Chatit.Server.Tables.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@SpringBootApplication
public class ServerApplication {

	@Autowired
	private UserRepo userRepo;

	@RequestMapping(value = "/login",method = RequestMethod.POST)
	Long Login(String Username,String Password){
		List<User> chklist =  userRepo.findByUname(Username);
		for (User user : chklist) {
			if(user.validatePassword(Password)){
				return user.getid();
			}
		}
		return -1L;
	}

	@RequestMapping(value="/register", method = RequestMethod.POST)
	Long Register(String Username,String Password,String Email){
		userRepo.save(new User(Username,Password,Email));
		List<User> chklist =  userRepo.findByUname(Username);
		for (User user : chklist) {
			if(user.validatePassword(Password)){
				return user.getid();
			}
		}
		return -1L;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ServerApplication.class, args);
	}

}
