package com.iplocation.controller;

import com.iplocation.IP2LocationUtils;
import com.iplocation.entites.LocationInfo;
import com.iplocation.entites.ResultIp2Location;
import com.iplocation.entites.ResultObject;
import com.iplocation.service.IpUtils;
import com.iplocation.service.Service;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpController {

    private final Logger LOGGER = LoggerFactory.getLogger(IpController.class);

    @GetMapping("/")
    public static ResultObject welcome(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject(0, "");
        resultObject.putData("message", "This is API for GPSocial Application");
        return resultObject;
    }
    @GetMapping("/showip")
    public static ResultObject showip(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject(0, "");
        String ip = IpUtils.getClientIpAIfExist(request);
        if (ip.isEmpty()) {
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage("Can't not Found IP");
        } else {
            resultObject.putData("ip", ip);
            Optional<LocationInfo> op = LocationInfo.getInstance(ip);
            if (op.isPresent()) {
                resultObject.putData("info", op.get());
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("Can't find LocationInfo");
            }

        }
        return resultObject;
    }

    @GetMapping("/online")
    public ResultObject getPeople() {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            resultObject.putData("onlineUsers", Service.ONLINE_USERS);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @GetMapping("/ip")
    public ResultObject ipInfo(@RequestParam(value = "ip", defaultValue = "") String ip) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            Optional<LocationInfo> op = LocationInfo.getInstance(ip);
            if (op.isPresent()) {
                resultObject.putData("info", op.get());
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("Can't find LocationInfo");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @GetMapping("/ip2location")
    public ResultObject ip2Location(@RequestParam(value = "ip", defaultValue = "") String ip) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            Optional<ResultIp2Location> op = IP2LocationUtils.get(ip);
            if (op.isPresent()) {
                ResultIp2Location resultIp2Location = op.get();
                if (resultIp2Location.getError() == ResultIp2Location.ERROR_IP2LOCATION.SUCCESS) {
                    resultObject.putData("ip2Location", resultIp2Location);
                } else {
                    resultObject.setError(ResultObject.ERROR);
                    resultObject.setMessage(resultIp2Location.getError().toString());
                }
            } else {
                resultObject.setError(ResultObject.ERROR);
                resultObject.setMessage("IP2LocationUtils get Empty");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    public static void main(String[] args) {
        IpController ipController = new IpController();
//        System.err.println(ipController.getLocationInfo("171.244.236.159"));
    }
}
