/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.controller;

import com.iplocation.entites.OnlineUser;
import com.iplocation.entites.ResultObject;
import com.iplocation.entites.UserAuthen;
import com.iplocation.entites.User;
import com.iplocation.service.Service;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author namhcn
 */
@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/user/register")
    public ResultObject registerUser(@RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "profilepiclink", defaultValue = "") String profilePicLink,
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestParam(value = "password", defaultValue = "") String password) {
        ResultObject resultObject = new ResultObject();
        try {
            if (password.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("password empty");
            } else if (username.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("username empty");
            } else if (email.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("email empty");
            } else {
                UserAuthen userAuthen = new UserAuthen(email, password);
                Optional<UserAuthen> op = Service.AUTHEN_FB.get(email);
                if (!op.isPresent()) {
                    Service.AUTHEN_FB.save(userAuthen);
                    User userFireBase = new User(email, profilePicLink, username, password);
                    Service.USER_FB.save(userFireBase);
                } else {
                    resultObject.setError(ResultObject.ERROR);
                    resultObject.setMessage("email exist");
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return resultObject;
    }

    @PostMapping("/user/login")
    public ResultObject login(HttpServletRequest request,
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "password", defaultValue = "") String password,
            HttpServletResponse response) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            if (password.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("email Empty");
                return resultObject;
            }
            if (password.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("password Empty");
                return resultObject;
            }

            Optional<UserAuthen> opUserAuthen = Service.AUTHEN_FB.get(email);
            if (opUserAuthen.isPresent()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("You have not signed up for an account with this email");
                return resultObject;
            }
            Optional<User> opUser = Service.USER_FB.getByField("email", email);
            if (!opUser.isPresent()) {
                String token = Base64.getEncoder().encodeToString(email.getBytes());
                User user = opUser.get();

                resultObject.putData("user", user);
                String IPv4 = IpController.getClientIpAddressIfServletRequestExist(request);
                if (IPv4.equals("127.0.0.1") || IPv4.equals("0:0:0:0:0:0:0:1")) {
                    IPv4 = IPS.get((Math.abs((int) (System.currentTimeMillis() % 1000)) % IPS.size()) - 1);
                }
                resultObject.putData("ip", IPv4);
                Cookie cookie = new Cookie("token", token);
                response.addCookie(cookie);
            } else {
                Service.AUTHEN_FB.deleteUser(email);
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("You have not signed up for an account with this email");
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(e.getMessage());
        }
        return resultObject;
    }

    @PostMapping("/user/logout")
    public ResultObject logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "username", defaultValue = "") String username) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            for (Cookie ck : request.getCookies()) {
                ck.setValue("");
                ck.setMaxAge(0);
                ck.setPath("/");
                response.addCookie(ck);
            }

        } catch (Exception e) {
            System.err.println(e);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(e.getMessage());
        }
        return resultObject;
    }

    @RequestMapping("/user/info")
    public ResultObject userInfo(@RequestParam(value = "token", defaultValue = "") String token) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            for (Map.Entry<String, OnlineUser> entry : Service.ONLINE_USERS.entrySet()) {
                String key = entry.getKey();
                OnlineUser onlineUser = entry.getValue();
                String email = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
                if (email.equals(onlineUser.getUser().getEmail())) {
                    resultObject.putData("user", onlineUser);
                    return resultObject;
                }
            }
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage("Not Found Token");

        } catch (Exception e) {
            System.err.println(e);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(e.getMessage());
        }
        return resultObject;
    }
    public static final List<String> IPS = new ArrayList<>();

    static {
        IPS.add("23.129.64.208");
        IPS.add("60.241.230.26");
        IPS.add("58.241.210.26");
        IPS.add("23.241.330.26");
        IPS.add("14.221.230.26");
        IPS.add("18.241.430.26");
        IPS.add("16.131.230.26");
        IPS.add("14.241.130.26");
        IPS.add("15.241.330.13");
        IPS.add("25.221.310.26");
        IPS.add("90.31.430.26");
    }

    public static void main(String[] args) {
        String aa = Base64.getEncoder().encodeToString("abc".getBytes());
        String s = new String(Base64.getDecoder().decode(aa), StandardCharsets.UTF_8);
        System.err.println(s);
    }
}