/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.restservice;

import com.ipcloation.constance.Const;
import com.iplocation.RestServiceApplication;
import com.iplocation.entites.UserAuthen;
import com.iplocation.service.Service;
import java.util.Optional;
import jdk.nashorn.internal.ir.annotations.Ignore;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author namhcn
 */
@SpringBootTest(classes = RestServiceApplication.class)
@AutoConfigureMockMvc
public class ServiceApplicationTests {

    @Ignore
    @Test
    public void userService() {
//        Optional<UserAuthen> opUserOptional = Service.AUTHEN_FB.get("etest@gmail.com");
//        assertThat(opUserOptional.isPresent()).isEqualTo(true);
    }

}
