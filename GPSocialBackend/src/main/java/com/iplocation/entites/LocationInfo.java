/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import com.ip2location.IPResult;
import com.iplocation.IP2LocationUtils;
import com.iplocation.controller.IpController;
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
        LocationInfo locationInfo = new LocationInfo();

        if (opIp2Location.isPresent()) {
            ResultIp2Location resultIp2Location = opIp2Location.get();
            if (resultIp2Location.getError() == ResultIp2Location.ERROR_IP2LOCATION.SUCCESS) {
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
        //Retry another API
        return LocationUtils.getLocationInfoAntherAPI(ip);
    }
}
