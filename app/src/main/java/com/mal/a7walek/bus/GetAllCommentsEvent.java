package com.mal.a7walek.bus;


import com.mal.a7walek.models.Comment;

import java.util.ArrayList;

/**
 * Created by OmarAli on 26/10/2016.
 */

public class GetAllCommentsEvent {
    ArrayList<Comment> workerComments;

    public GetAllCommentsEvent(ArrayList<Comment> workerComments) {
        this.workerComments = workerComments;
    }

    public ArrayList<Comment> getWorkerComments() {
        return workerComments;
    }
}
