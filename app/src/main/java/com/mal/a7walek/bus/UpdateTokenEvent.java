package com.mal.a7walek.bus;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class UpdateTokenEvent {
    String newToken;

    public UpdateTokenEvent(String newToken) {
        this.newToken = newToken;
    }

    public String getNewToken() {
        return newToken;
    }
}
