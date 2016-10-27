package com.mal.a7walek.models;

import java.util.HashMap;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class Job {
    String image_url , description , category , user_token , address;
    double lat,lng;
    String key;


    public Job(){

    }

    public Job(String image_url, String description, String category, String user_token, String address, double lat, double lng) {
        this.image_url = image_url;
        this.description = description;
        this.category = category;
        this.user_token = user_token;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getUser_token() {
        return user_token;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void BuildJob(HashMap<String,Object>builder){
        this.image_url = (String)builder.get("image_url");
        this.description = (String)builder.get("description");
        this.category = (String)builder.get("category");
        this.user_token = (String)builder.get("user_token");
        this.address = (String)builder.get("address");
        this.lat = (Double)builder.get("lat");
        this.lng = (Double)builder.get("lng");
    }
}
