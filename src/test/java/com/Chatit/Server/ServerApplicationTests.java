package com.Chatit.Server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    public void contextLoads() {
        assertThat(MainApp).isNotNull();
        assertThat(userRepo).isNotNull();
        assertThat(msgRepo).isNotNull();
        assertThat(usrchatrepo).isNotNull();
    }
}
