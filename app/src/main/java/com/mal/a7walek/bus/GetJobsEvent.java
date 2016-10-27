package com.mal.a7walek.bus;

import com.mal.a7walek.models.Job;

import java.util.ArrayList;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class GetJobsEvent {
    ArrayList<Job>jobs = new ArrayList<>();
    boolean hasJobs = true;

    public GetJobsEvent(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public boolean isHasJobs() {
        return hasJobs;
    }
}
