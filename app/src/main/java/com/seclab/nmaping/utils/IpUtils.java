package com.seclab.nmaping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtils {


    public static String getLocalIPAddress() throws SocketException {
        for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
            NetworkInterface intf = en.nextElement();
            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                InetAddress inetAddress = enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        return "null";
    }
    public static boolean innerIP(String ip) {

        Pattern reg = Pattern.compile("^(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        Matcher match = reg.matcher(ip);

        return match.find();
    }



    public static String getInnerIPSet(Context context) throws SocketException{

        String[] strings = getLocalIPAddress().split("\\.");
        strings[3] = "0";




        return strings[0]+"."+strings[1]+"."+strings[2]+"."+strings[3]+"/"+getIpBinary(context);

    }


    /**
     * 获取IP地址或掩码二进制数组
     * @param
     * @return 二进制数组如[11111111,11111111,11111111,11111111]
     */
    public static int getIpBinary(Context context){

        @SuppressLint("WrongConstant") WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        String ip = intToIp(dhcpInfo.netmask);

        String[] strs = ip.split("\\.");
        String temp = "";
        int count = 0;
        for (int i = 0; i < 4; i++){
            strs[i] = Integer.toBinaryString(Integer.parseInt(strs[i]));

                temp += strs[i];

        }
        temp = temp.replace("0","");


        return temp.length();
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
