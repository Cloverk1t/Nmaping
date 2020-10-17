package com.seclab.nmaping.bean;

public class ScanBean {

    private String ipName;
//    private String macAddr;

    public ScanBean (String ipName){
        this.ipName = ipName;
//        this.macAddr = macAddr;
    }

    public ScanBean(Object o) {
    }

    public String getIpName() {
        return ipName;
    }

    public void setIpName(String ipName) {
        this.ipName = ipName;
    }

//    public String getMacAddr() {
//        return macAddr;
//    }
//
//    public void setMacAddr(String macAddr) {
//        this.macAddr = macAddr;
//    }
}
