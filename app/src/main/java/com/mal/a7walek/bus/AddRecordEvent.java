package com.mal.a7walek.bus;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class AddRecordEvent {
    boolean success;

    public AddRecordEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
