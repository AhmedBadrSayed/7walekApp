package com.mal.a7walek.models;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class Comment {
    String workerToken,jobToken,comment,price;
    Worker worker;

    public Comment(){

    }

    public Comment(String workerToken, String jobToken, String comment, String price) {
        this.workerToken = workerToken;
        this.jobToken = jobToken;
        this.comment = comment;
        this.price = price;
    }

    public String getWorkerToken() {
        return workerToken;
    }

    public String getJobToken() {
        return jobToken;
    }

    public String getComment() {
        return comment;
    }

    public String getPrice() {
        return price;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
}
