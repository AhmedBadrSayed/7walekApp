package com.mal.a7walek.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class User {

    String userName , token , image_url , address , phoneNumber;
    double lat , lng ;
    HashMap<String,Object> jobs ;


    public User(){

    }

    public User(String userName, String token, String image_url, String address, double lat, double lng, String phoneNumber) {
        this.userName = userName;
        this.token = token;
        this.image_url = image_url;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Map<String,Object> getJobReference() {
        return jobs;
    }

    public void setJobs(HashMap<String, Object> jobs) {
        this.jobs = jobs;
    }

    public User BuildUser(Map<String,Object> builder){
        this.userName = (String)builder.get("userName");
        this.token = (String)builder.get("token");
        this.image_url = (String)builder.get("image_url");
        this.address = (String)builder.get("address");
        this.lat = (Double)builder.get("lat");
        this.lng = (Double) builder.get("lng");
        this.phoneNumber = (String)builder.get("phoneNumber");
        setJobs((HashMap<String, Object>) builder.get("jobs"));

        return this;
    }
}
