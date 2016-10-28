package com.mal.a7walek.DataObjects;

/**
 * Created by Ahmed Badr on 28/10/2016.
 */
public class WorkerRequest {

    public String clientName;
    public String clientDescription;
    public int clientPhoto;

    public WorkerRequest(String clientName, String clientDescription, int clientPhoto) {
        this.clientName = clientName;
        this.clientDescription = clientDescription;
        this.clientPhoto = clientPhoto;
    }

}
