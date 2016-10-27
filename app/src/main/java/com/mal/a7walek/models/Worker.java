package com.mal.a7walek.models;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class Worker extends User{

    String id_url , profession;


    public Worker(){

    }

    public Worker(String userName, String token, String image_url, String address, double lat, double lng, String phoneNumber
                    , String id_url , String profession) {
        super(userName, token, image_url, address, lat, lng, phoneNumber);

        this.id_url = id_url;
        this.profession = profession;
    }

    public String getId_url() {
        return id_url;
    }

    public String getProfession() {
        return profession;
    }
}
