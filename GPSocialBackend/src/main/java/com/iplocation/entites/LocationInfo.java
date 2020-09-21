/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

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
public class LocationInfo {

    private String country_code;
    private String country_name;
    private String city;
    private String postal;
    private float latitude;
    private float longitude;
    private String IPv4;
    private String state;
}
