package com.mal.a7walek.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class User implements Parcelable{

    String userName , token , image_url , address , phoneNumber , email;
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

    protected User(Parcel in) {
        userName = in.readString();
        token = in.readString();
        image_url = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(token);
        parcel.writeString(image_url);
        parcel.writeString(address);
        parcel.writeString(phoneNumber);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }
}
