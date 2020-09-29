/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.iplocation.controller.IpController;
import com.iplocation.controller.UserController;
import com.iplocation.entites.LocationInfo;
import com.iplocation.entites.OnlineUser;
import com.iplocation.entites.UserAuthen;
import com.iplocation.entites.User;
import com.iplocation.service.cache.GuavaCache;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 */
@org.springframework.stereotype.Service
public class Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    public static IDataOperate<User> USER_FB;
    public static IDataOperate<UserAuthen> AUTHEN_FB;
    public static Map<String, OnlineUser> ONLINE_USERS = new ConcurrentHashMap<>();
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static int EXPIRE_TIME = 10 * 1000;
    private static final Timer timer = new Timer(); // creating timer

    static {
//        USER_FB = new FirebaseCRUD<User>(User.class, "Users") {};
        USER_FB = new GuavaCache<>(new FirebaseCRUD<>(User.class, "Users"));
        AUTHEN_FB = new GuavaCache<>(new FirebaseCRUD<>(UserAuthen.class, "UserAthen"));
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                ONLINE_USERS.entrySet().forEach(entry -> {
                    String email = entry.getKey();
                    OnlineUser onlineUser = entry.getValue();
                    if (System.currentTimeMillis() - onlineUser.getLastPing() > EXPIRE_TIME) {
//                        ONLINE_USERS.remove(email);
//                        System.err.println("removeUser:" + email);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(tt, 0, 2000);

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

    public static void main(String[] args) {

    }
}
