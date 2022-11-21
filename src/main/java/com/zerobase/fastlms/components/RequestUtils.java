package com.zerobase.fastlms.components;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null) ip = req.getRemoteAddr();
        return ip;
    }

    public static String getUserAgent(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        return userAgent;
    }
}
