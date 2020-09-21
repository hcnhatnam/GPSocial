/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.iplocation.entites.OnlineUser;
import com.iplocation.entites.UserAuthen;
import com.iplocation.entites.User;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author namhcn
 */
public class Service {

    public static FirebaseCRUD<User> USER_FB;
    public static FirebaseCRUD<UserAuthen> AUTHEN_FB;
    public static Map<String, OnlineUser> ONLINE_USERS = new HashMap<>();

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
    }
   
    public static void main(String[] args) {

    }
}
