package com.mal.a7walek.bus;

import com.mal.a7walek.models.Worker;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class GetWorkerEvent {

    Worker worker;
    boolean isWorkerFound = true;

    public GetWorkerEvent(Worker worker) {
        this.worker = worker;
    }

    public GetWorkerEvent(boolean isWorkerFound) {
        this.isWorkerFound = isWorkerFound;
    }

    public Worker getWorker() {
        return worker;
    }

    public boolean isWorkerFound() {
        return isWorkerFound;
    }
}
