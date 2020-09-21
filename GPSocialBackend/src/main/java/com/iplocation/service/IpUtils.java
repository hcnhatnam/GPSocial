/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.service;

import com.iplocation.CORSFilter;
import static com.iplocation.controller.UserController.IPS;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author namhcn
 */
public class IpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    public static String getClientIpAIfExist(HttpServletRequest request) {
        String ip = "";
        try {
            for (String header : IP_HEADER_CANDIDATES) {
                String ipList = request.getHeader(header);
                if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                    ip = ipList.split(",")[0];
                    break;
                }
            }
            if (ip.isEmpty()) {
                ip = request.getRemoteAddr();
                if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                    ip = IPS.get((Math.abs((int) (System.currentTimeMillis() % 1000)) % IPS.size()) - 1);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return ip;
    }
}
