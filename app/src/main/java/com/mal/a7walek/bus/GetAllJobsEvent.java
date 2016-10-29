package com.mal.a7walek.bus;


import com.mal.a7walek.models.Job;

import java.util.ArrayList;

/**
 * Created by OmarAli on 27/10/2016.
 */

public class GetAllJobsEvent {
    ArrayList<Job> jobs = new ArrayList<>();
    boolean hasJobs = true;

    public GetAllJobsEvent(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public boolean isHasJobs() {
        return hasJobs;
    }
}
