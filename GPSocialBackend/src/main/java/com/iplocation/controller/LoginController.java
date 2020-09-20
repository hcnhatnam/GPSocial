/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.controller;

import com.iplocation.entites.ResultObject;
import com.iplocation.entites.UserApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class LoginController {

    private static final Map<String, String> USER_LOGIN = new ConcurrentHashMap<>();
    public static final Map<String, UserApp> USER_OF_APP = new ConcurrentHashMap<>();
    public static final List<String> IPS = new ArrayList<>();

    static {
        USER_LOGIN.put("admin", "1");
        USER_LOGIN.put("user", "2");
        USER_LOGIN.put("aa", "2");
        USER_LOGIN.put("bb", "2");
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
    public static Random rd = new Random();

    @PostMapping("/user/login")
    public ResultObject login(
            HttpServletRequest request,
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestParam(value = "password", defaultValue = "") String password,
            @RequestParam(value = "IPv4", defaultValue = "") String IPv4,
            @RequestParam(value = "city", defaultValue = "") String city,
            @RequestParam(value = "country_code", defaultValue = "") String country_code,
            @RequestParam(value = "latitude", defaultValue = "0.0") Double latitude,
            @RequestParam(value = "longitude", defaultValue = "0.0") Double longitude,
            @RequestParam(value = "state", defaultValue = "") String state,
            HttpServletResponse response) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            IPv4 = IpController.getClientIpAddressIfServletRequestExist(request);
            if (USER_LOGIN.get(username) != null) {
                String token = username;
                UserApp userApp = new UserApp(username, password,
                        "Hello guy!", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
                        IPv4, token);
                USER_OF_APP.put(username, userApp);
                resultObject.putData("user", userApp);
                Cookie cookie = new Cookie("token", userApp.getToken());
                //add cookie to response
                response.addCookie(cookie);
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("User not register");
            }

        } catch (Exception e) {
            System.err.println(e);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(e.getMessage());
        }
        return resultObject;
    }

    @PostMapping("/user/logout")
    public ResultObject logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "username", defaultValue = "") String username) {
        USER_OF_APP.remove(username);
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
            for (Map.Entry<String, UserApp> entry : USER_OF_APP.entrySet()) {
                String key = entry.getKey();
                UserApp ua = entry.getValue();
                if (ua.getToken().equals(token)) {
                    resultObject.putData("user", ua);
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

}
