package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpUtils {

    public static String getUrl(String baseUrl, String endpoint) {
        return baseUrl + "/" + endpoint;
    }

    public static String getUrlWithService(String baseUrl, String service) {
        return baseUrl + "." + service;
    }

    public static String mask4X6(String string) {
        final int beginUnmaskedCount = 4;
        final int endUnmaskedCount = 6;
        final int unmaskedCount = beginUnmaskedCount + endUnmaskedCount;

        int stringLength = string.length();
        StringBuilder sb = new StringBuilder();

        if (stringLength <= unmaskedCount) {
            sb.repeat('*', stringLength);
            return sb.toString();
        }

        return sb.append(string, 0, beginUnmaskedCount)
                .repeat('*', stringLength - unmaskedCount)
                .append(string, stringLength - endUnmaskedCount, stringLength)
                .toString();
    }
}