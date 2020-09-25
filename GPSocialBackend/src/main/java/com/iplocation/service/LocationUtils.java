/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import static com.iplocation.controller.UserController.IPS;
import com.iplocation.entites.LocationInfo;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author namhcn
 */
public class LocationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUtils.class);

    private static final String API_LOCATION = "https://geolocation-db.com/json/697de680-a737-11ea-9820-af05f4014d91";

    public static Optional<LocationInfo> getLocationInfoAntherAPI(String ip) {
        try {

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
            return Optional.of(locationInfo);

        } catch (JsonSyntaxException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        System.err.println(gson.toJson(getLocationInfoAntherAPI("14.241.230.44")));
    }

}
