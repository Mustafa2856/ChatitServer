package com.Chatit.Server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ServerApplicationTests {

    @Autowired
    private ServerApplication MainApp;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo msgRepo;
    @Autowired
    private UsrChatrepo usrchatrepo;

    @Test
    public void contextLoads(){
        assertThat(MainApp).isNotNull();
        assertThat(userRepo).isNotNull();
        assertThat(msgRepo).isNotNull();
        assertThat(usrchatrepo).isNotNull();
    }

    @Test
    public void Datapass(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        //Assertions.assertNotEquals(-1,MainApp.Login(request,"abc@a.com","123"));
        //Assertions.assertEquals(-1,MainApp.Register(request,"newusr","123","abc@a.com"));
        //assertThat(MainApp.getPendingChats(request,"abc@a.com","123")).isNotNull();
    }
}
