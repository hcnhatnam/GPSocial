/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.controller;

import com.iplocation.entites.LocationInfo;
import com.iplocation.entites.OnlineUser;
import com.iplocation.entites.ResultObject;
import com.iplocation.entites.UserAuthen;
import com.iplocation.entites.User;
import com.iplocation.service.IpUtils;
import com.iplocation.service.Service;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/user/onlineusers")
    public ResultObject ping(HttpServletRequest request, @RequestParam(value = "email", defaultValue = "") String email) {
        ResultObject resultObject = new ResultObject();
        try {

            if (email.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("Email Not Found");
            }
            OnlineUser onlineUser = Service.ONLINE_USERS.get(email);
            if (onlineUser != null) {
                Optional<LocationInfo> opLocationInfo = LocationInfo.getInstance(request);
                onlineUser.setLastPing(System.currentTimeMillis());
                if (opLocationInfo.isPresent()) {
                    onlineUser.setLocationInfo(opLocationInfo.get());
                }
                resultObject.putData("onlineUsers", Service.ONLINE_USERS);
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage(String.format("User with email %s not Found!", email));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @PostMapping("/user/register")
    public ResultObject registerUser(@RequestParam(value = "email", defaultValue = "https://icons.iconarchive.com/icons/papirus-team/papirus-status/512/avatar-default-icon.png") String email,
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
                boolean isPresent = true;
                UserAuthen userAuthen = new UserAuthen(email, password);
                Optional<UserAuthen> op = Service.AUTHEN_FB.get(email);
                if (!op.isPresent()) {
                    Service.AUTHEN_FB.save(userAuthen);
                    isPresent = false;
                }

                Optional<User> opUser = Service.USER_FB.getByField("email", email);
                if (!opUser.isPresent()) {
                    User userFireBase = new User(email, profilePicLink, username);
                    Service.USER_FB.save(userFireBase);
                    isPresent = false;

                }
                if (isPresent) {
                    resultObject.setError(ResultObject.ERROR);
                    resultObject.setMessage("email exist");
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
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
            if (email.isEmpty()) {
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
            if (!opUserAuthen.isPresent()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("You have not signed up for an account with this email");
                return resultObject;
            }
            Optional<User> opUser = Service.USER_FB.getByField("email", email);
            if (opUser.isPresent()) {
                String token = Base64.getEncoder().encodeToString(email.getBytes());
                User user = opUser.get();

                resultObject.putData("user", user);
                String ip = IpUtils.getClientIpAIfExist(request);
                OnlineUser onlineUser = new OnlineUser(user, System.currentTimeMillis(), ip);
                Optional<LocationInfo> op = LocationInfo.getInstance(ip);
                if (op.isPresent()) {
                    onlineUser.setLocationInfo(op.get());
                    resultObject.putData("locationinfo", onlineUser.getLocationInfo());
                }

                Service.ONLINE_USERS.put(email, onlineUser);
                resultObject.putData("ip", ip);
                resultObject.putData("token", token);
                Cookie cookie = new Cookie("token", token);
                response.addCookie(cookie);
            } else {
//                Service.AUTHEN_FB.deleteUser(email);
//                resultObject.setError(ResultObject.ERROR);
//                resultObject.setMessage("You have not signed up for an account with this email");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @PostMapping("/user/logout")
    public ResultObject logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "email", defaultValue = "") String email) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            if (email.isEmpty()) {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("email in param Empty!");
            } else {
                OnlineUser onlineUser = Service.ONLINE_USERS.get(email);
                if (onlineUser != null) {
                    Service.ONLINE_USERS.remove(email);
                } else {
                    resultObject.setError(ResultObject.ERROR);
                    resultObject.setMessage(String.format("Online User (%) not found!", email));
                }
                for (Cookie ck : request.getCookies()) {
                    ck.setValue("");
                    ck.setMaxAge(0);
                    ck.setPath("/");
                    response.addCookie(ck);
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @RequestMapping("/user/info")
    public ResultObject userInfo(HttpServletResponse response, @CookieValue(value = "token", defaultValue = "") String token, @RequestParam(value = "token", defaultValue = "") String tokenParam) {
        ResultObject resultObject = new ResultObject(0, "");

        try {
            if (token.isEmpty()) {
                token = tokenParam;
                if (!tokenParam.isEmpty()) {
                    LOGGER.info("TOKEN PARAM:" + tokenParam);
                    Cookie cookie = new Cookie("token", tokenParam);
                    response.addCookie(cookie);
                } else {
                    LOGGER.info("ALL TOKEN isEMpty:");
                }
            }
            if (!token.isEmpty()) {
                String email = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
                OnlineUser onlineUser = Service.ONLINE_USERS.get(email);
                if (onlineUser != null && onlineUser.getUser() != null) {
                    if (email.equals(onlineUser.getUser().getEmail())) {
                        resultObject.putData("user", onlineUser);
                        return resultObject;
                    } else {
                        resultObject.setError(ResultObject.ERROR);
                        resultObject.setMessage("Server has problem");
                    }
                } else {
                    resultObject.setError(ResultObject.ERROR);
                    resultObject.setMessage("User has removed online user list");
                }

            } else {

                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("Not Found Token");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @GetMapping("/user/id")
    public ResultObject getUserById(@RequestParam(value = "id", defaultValue = "") String id) {
        ResultObject resultObject = new ResultObject();
        if (id.isEmpty()) {
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage("id is Empty");
        } else {
            Optional<User> opUser = Service.USER_FB.get(id);
            if (opUser.isPresent()) {
                resultObject.putData("user", opUser.get());
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("User not found");
            }
        }
        return resultObject;
    }

    @GetMapping("/user/email")
    public ResultObject getUserByEmail(@RequestParam(value = "email", defaultValue = "") String email) {
        ResultObject resultObject = new ResultObject();
        if (email.isEmpty()) {
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage("email is Empty");
        } else {
            Optional<User> opUser = Service.USER_FB.getByField("email", email);
            System.err.println(email);
            if (opUser.isPresent()) {
                resultObject.putData("user", opUser.get());
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("User not found");
            }
        }
        return resultObject;
    }
    public static final List<String> IPS = new ArrayList<>();
    public static final List<String> EMAIL_USERS = new ArrayList<>();

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
        IPS.add("87.11.430.26");
        IPS.add("90.112.430.26");
        IPS.add("20.114.430.26");
        IPS.add("30.115.430.26");
        IPS.add("75.116.430.26");
        IPS.add("10.11.230.26");
        IPS.add("10.61.230.36");
        IPS.add("10.01.230.46");
        IPS.add("35.41.230.56");
        IPS.add("10.31.230.96");

        EMAIL_USERS.add("nam@gmail.com");
        EMAIL_USERS.add("duy@gmail.com");
        EMAIL_USERS.add("etest@gmail.com");
        EMAIL_USERS.add("nhat@gmail.com");

    }

    public static void main(String[] args) {
        System.err.println(Base64.getEncoder().encodeToString("nam@gmail.com.vn".getBytes()));
    }
}
