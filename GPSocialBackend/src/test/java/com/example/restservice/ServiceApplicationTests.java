/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.restservice;

import com.iplocation.RestServiceApplication;
import com.iplocation.service.UserFireBaseService;
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
    
    @Autowired
    UserFireBaseService userFireBaseService;
    
    @Test
    public void userService() {
        UserFireBaseService service = new UserFireBaseService();
        service.getAllUser().forEach((t) -> {
            System.out.println(t.toString());
        });
//        UserFireBase userFireBase = new UserFireBase("nhatnam1@gmail.com", "https://f11.photo.talk.zdn.vn/2583027883507930654/17ef1c54daa525fb7cb4.jpg", "namhcn");
//        service.saveUser(userFireBase);

    }
}
