package com.mal.a7walek.DataObjects;

/**
 * Created by Ahmed Badr on 27/10/2016.
 */
public class ClientRequestsDetails {

    public String workerName;
    public String requestDescription;
    public int requestPhoto;
    public double price;

    public ClientRequestsDetails(String workerName, String requestDescription, int requestPhoto, double price) {
        this.workerName = workerName;
        this.requestDescription = requestDescription;
        this.requestPhoto = requestPhoto;
        this.price = price;
    }
}
