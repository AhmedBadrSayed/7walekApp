package com.mal.a7walek.bus;

/**
 * Created by OmarAli on 27/10/2016.
 */

public class UploadImageEvent {
    String url;

    public UploadImageEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
