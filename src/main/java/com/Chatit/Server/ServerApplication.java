package com.Chatit.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@SpringBootApplication
public class ServerApplication {

	@RequestMapping(value = "/login",method = RequestMethod.POST)
	String Login(String Username,String Password){
		return "Username"+Username + "Password"+Password;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ServerApplication.class, args);
	}

}
