package com.seclab.nmaping.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NmapFormat {

    public static List<String> NmapResFormat(String s) {

        String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
        List<String> ips = new ArrayList<String>();
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        while (m.find()) {
            String result = m.group();
            ips.add(result);
        }
        return ips;

    }


}
