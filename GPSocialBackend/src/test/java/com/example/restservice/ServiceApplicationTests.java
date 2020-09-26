/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.restservice;

import com.google.gson.Gson;
import com.iplocation.RestServiceApplication;
import com.iplocation.entites.User;
import com.iplocation.entites.UserAuthen;
import com.iplocation.service.Service;
import static com.iplocation.service.Service.scheduledExecutorService;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author namhcn
 */
@SpringBootTest(classes = RestServiceApplication.class)
@AutoConfigureMockMvc
public class ServiceApplicationTests {

//    @Autowired
//    UserFireBaseService userFireBaseService;
//    
    @Test
    public void userService() {
        Gson gson = new Gson();
//        Optional<UserAuthen> opUserAuthen = Service.AUTHEN_FB.get("nhatnam@gmail.com");
//        Optional<User> opUserAuthen = Service.USER_FB.getByField("email", "nhatnam@gmail.com");
//
//        if (opUserAuthen.isPresent()) {
//            System.err.println(gson.toJson(opUserAuthen.get()));
//        }
//        System.err.println(Service.AUTHEN_FB.get("nhatnam@gmail.com"));
//        Service.initExampleUser("atest@gmail.com", "10.30.58.78");
//        System.err.println(Service.ONLINE_USERS.get("atest@gmail.com"));
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.err.println("-=========");
            }
        }, 1, TimeUnit.SECONDS);
        while (true) {

        }
    }
}
