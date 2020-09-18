package com.iplocation.controller;

import com.iplocation.Greeting;
import com.google.gson.Gson;
import com.iplocation.CORSFilter;
import com.iplocation.IP2LocationUtils;
import com.iplocation.entites.LocationInfo;
import com.iplocation.entites.ResultIp2Location;
import com.iplocation.entites.ResultObject;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class IpController {

    private final Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final String API_LOCATION = "https://geolocation-db.com/json/697de680-a737-11ea-9820-af05f4014d91";

    public LocationInfo getLocationInfo(String ip) {
        WebClient client3 = WebClient
                .builder()
                .baseUrl(String.format("%s/%s", API_LOCATION, ip))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        String response2 = client3.get().exchange()
                .block()
                .bodyToMono(String.class)
                .block();
        Gson g = new Gson();
        LocationInfo locationInfo = g.fromJson(response2, LocationInfo.class);
        return locationInfo;
    }

    @GetMapping("/people")
    public ResultObject getPeople() {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            resultObject.putData("userofapp", LoginController.USER_OF_APP);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            resultObject.setError(ResultObject.ERROR);
            resultObject.setMessage(ex.getMessage());
        }
        return resultObject;
    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/ip")
    public ResultObject ip(@RequestParam(value = "ip", defaultValue = "") String ip) {
        ResultObject resultObject = new ResultObject(0, "");
        try {
            resultObject.putData("ips", getLocationInfo(ip));

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
        System.err.println(ipController.getLocationInfo("171.244.236.159"));
    }
}
