package com.mal.a7walek.DataObjects;


/**
 * Created by Ahmed Badr on 1/9/2016.
 */
public class ClientRequest {

    public String clientName;
    public String requestDescription;
    public int requestPhoto;

    public ClientRequest(String clientName, String requestDescription, int requestPhoto) {
        this.clientName = clientName;
        this.requestDescription = requestDescription;
        this.requestPhoto = requestPhoto;
    }
}
