/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import com.ip2location.IPResult;
import com.iplocation.IP2LocationUtils;
import com.iplocation.controller.IpController;
import com.iplocation.controller.UserController;
import com.iplocation.service.IpUtils;
import com.iplocation.service.LocationUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
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

    private LocationInfo() {

    }

    public static Optional<LocationInfo> getInstance(HttpServletRequest request) {
        String ip = IpUtils.getClientIpAIfExist(request);
        if (!ip.isEmpty()) {
            return getInstance(ip);
        }
        return Optional.empty();
    }

    public static Optional<LocationInfo> getInstance(String ip) {
        Optional<ResultIp2Location> opIp2Location = IP2LocationUtils.get(ip);
        LocationInfo locationInfo = null;

        if (opIp2Location.isPresent()) {
            ResultIp2Location resultIp2Location = opIp2Location.get();
            if (resultIp2Location.getError() == ResultIp2Location.ERROR_IP2LOCATION.SUCCESS) {
                locationInfo = new LocationInfo();
                IPResult iPResult = resultIp2Location.getIPResult();
                locationInfo.setCity(iPResult.getCity());
                locationInfo.setIPv4(ip);
                locationInfo.setLatitude(iPResult.getLatitude());
                locationInfo.setLongitude(iPResult.getLongitude());
                locationInfo.setState("");
                locationInfo.setCountry_code(iPResult.getZipCode());
                locationInfo.setCountry_name(iPResult.getCountryShort());
                return Optional.of(locationInfo);
            }
        }

        //hard code for Test (The resion using 1 machine for test)
        //Retry another API
        if (locationInfo == null) {
            locationInfo = LocationUtils.getLocationInfoAntherAPI(ip).get();
        }
        if (UserController.IPS.contains(ip)) {
            locationInfo.setLongitude(ip.hashCode() % 85);
            locationInfo.setLatitude(ip.hashCode() % 50);

        }
        return Optional.of(locationInfo);

    }
}
