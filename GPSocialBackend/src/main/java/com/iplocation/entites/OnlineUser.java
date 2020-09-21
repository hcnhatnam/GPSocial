/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OnlineUser {

    private User user;
    private long lastPing;
    private String ip;
    private LocationInfo locationInfo;
    private Map<String, String> extenInfo;

    public OnlineUser(User user, long lastPing, String ip) {
        this.user = user;
        this.lastPing = lastPing;
        this.ip = ip;
        extenInfo = new HashMap<>();
    }

    public OnlineUser(User user, long lastPing, LocationInfo locationInfo) {
        this.user = user;
        this.lastPing = lastPing;
        this.locationInfo = locationInfo;
        extenInfo = new HashMap<>();
    }

}
