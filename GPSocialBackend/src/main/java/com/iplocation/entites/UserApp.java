/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
@Getter
@Setter
@AllArgsConstructor
public class UserApp {

    private String name;
    private String roles;
    private String introduction;
    private String avatar;
    private String ip;
    private String token;
}
