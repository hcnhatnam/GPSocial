/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.iplocation.entites.ResultIp2Location;
import com.iplocation.entites.ResultIp2Location.ERROR_IP2LOCATION;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 */
public class IP2LocationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IP2Location.class);

    private static final String BIN_FILE = "libs/IP2LOCATION-LITE-DB11.IPV6.BIN/IP2LOCATION-LITE-DB11.IPV6.BIN";
    private static final IP2Location ip2Location;

    static {
        ip2Location = new IP2Location();
        ip2Location.IPDatabasePath = BIN_FILE;

    }


    public static Optional<ResultIp2Location> get(String ip) {
        try {
            IPResult rec = ip2Location.IPQuery(ip);
            ERROR_IP2LOCATION error;
            if ("OK".equals(rec.getStatus())) {
                error = ERROR_IP2LOCATION.SUCCESS;
            } else if ("EMPTY_IP_ADDRESS".equals(rec.getStatus())) {
                error = ERROR_IP2LOCATION.EMPTY_IP_ADDRESS;
            } else if ("INVALID_IP_ADDRESS".equals(rec.getStatus())) {
                error = ERROR_IP2LOCATION.INVALID_IP_ADDRESS;
            } else if ("MISSING_FILE".equals(rec.getStatus())) {
                error = ERROR_IP2LOCATION.MISSING_FILE;
            } else if ("IPV6_NOT_SUPPORTED".equals(rec.getStatus())) {
                error = ERROR_IP2LOCATION.IPV6_NOT_SUPPORTED;
            } else {
                error = ERROR_IP2LOCATION.UNKNOW_ERROR;
            }
            ResultIp2Location resultIp2Location = new ResultIp2Location(error, rec);
            return Optional.of(resultIp2Location);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public static void main(String args[]) throws Exception {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("System IP Address : "
                + (localhost.getHostAddress()).trim());
        String systemipaddress = "";
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc
                    = new BufferedReader(new InputStreamReader(url_name.openStream()));

            get(systemipaddress);
        } catch (IOException e) {
            systemipaddress = "Cannot Execute Properly";
        }
        System.out.println("Public IP Address: " + systemipaddress + "\n");
    }

}
