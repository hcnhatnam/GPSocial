/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.iplocation.controller.UserController;
import com.iplocation.entites.LocationInfo;
import com.iplocation.entites.OnlineUser;
import com.iplocation.entites.UserAuthen;
import com.iplocation.entites.User;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author namhcn
 */
public class Service {

    public static FirebaseCRUD<User> USER_FB;
    public static FirebaseCRUD<UserAuthen> AUTHEN_FB;
    public static Map<String, OnlineUser> ONLINE_USERS = new HashMap<>();
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static int EXPIRE_TIME = 10 * 60 * 1000;

    static {
        USER_FB = new FirebaseCRUD<User>(User.class, "Users") {
            @Override
            public String getId(User obj) {
                return obj.get_id();
            }
        };
        AUTHEN_FB = new FirebaseCRUD<UserAuthen>(UserAuthen.class, "UserAthen") {
            @Override
            public String getId(UserAuthen obj) {
                return obj.getEmail();
            }
        };
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                ONLINE_USERS.entrySet().forEach(entry -> {
                    String email = entry.getKey();
                    OnlineUser onlineUser = entry.getValue();
                    if (System.currentTimeMillis() - onlineUser.getLastPing() > EXPIRE_TIME) {
                        ONLINE_USERS.remove(email);
                    }

                });
            }
        }, 1, TimeUnit.SECONDS);
        initExampleUser("atest@gmail.com", UserController.IPS.get(Math.abs("atest@gmail.com".hashCode()) % UserController.IPS.size() - 1));
        initExampleUser("duy@gmail.com", UserController.IPS.get(Math.abs("duy@gmail.com".hashCode()) % UserController.IPS.size() - 1));

    }

    public static void initExampleUser(String email, String ip) {
        Optional<User> opUser = Service.USER_FB.getByField("email", email);
        if (opUser.isPresent()) {
            User user = opUser.get();
            OnlineUser onlineUser = new OnlineUser(user, System.currentTimeMillis() * 3, ip);
            Optional<LocationInfo> op = LocationInfo.getInstance(ip);
            if (op.isPresent()) {
                onlineUser.setLocationInfo(op.get());
                Service.ONLINE_USERS.put(email, onlineUser);
            }
        }
    }
}
