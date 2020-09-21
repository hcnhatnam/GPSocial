/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.restservice;

import com.iplocation.RestServiceApplication;
import com.iplocation.service.Service;
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
        System.err.println(Service.AUTHEN_FB.get("nhatnam@gmail.com"));

    }
}
