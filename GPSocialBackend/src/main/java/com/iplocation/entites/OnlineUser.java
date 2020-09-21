/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

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
    private LocationInfo locationInfo;
    private Map<String, String> extenInfo;

}
