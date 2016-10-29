package com.mal.a7walek.bus;


import com.mal.a7walek.models.User;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class GetUserEvent {

    User user;
    boolean isUserFound = true;

    public GetUserEvent(User user) {
        this.user = user;
    }

    public GetUserEvent(boolean isUserFound) {
        this.isUserFound = isUserFound;
    }

    public User getUser() {
        return user;
    }

    public boolean isUserFound() {
        return isUserFound;
    }
}
