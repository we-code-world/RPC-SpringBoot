package com.wangdi.servicecenter;

public class Service {
    String name;
    String version;
    String address;
    int port;

    public Service(String name, String version, String address, int port){
        this.name = name;
        this.version = version;
        this.address  = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
