/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import com.ip2location.IPResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultIp2Location {

    public static enum ERROR_IP2LOCATION {
        SUCCESS,
        EMPTY_IP_ADDRESS,
        INVALID_IP_ADDRESS,
        MISSING_FILE,
        IPV6_NOT_SUPPORTED,
        UNKNOW_ERROR
    }

    private ERROR_IP2LOCATION error;
    private IPResult iPResult;
}
