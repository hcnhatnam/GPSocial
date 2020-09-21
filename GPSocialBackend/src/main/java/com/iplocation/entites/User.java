/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import com.google.gson.Gson;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
@NoArgsConstructor
@Getter
@Setter
public class User {

    private static final Gson gson = new Gson();

    private String _id;
    private String email;
    private String profilePicLink;
    private String username;

    public User(String email, String profilePicLink, String username) {
        this._id = UUID.randomUUID().toString();
        this.email = email;
        this.profilePicLink = profilePicLink;
        this.username = username;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
