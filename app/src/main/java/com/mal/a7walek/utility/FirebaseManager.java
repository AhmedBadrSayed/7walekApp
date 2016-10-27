package com.mal.a7walek.utility;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.ErrorEvent;
import com.mal.a7walek.bus.GetUserEvent;
import com.mal.a7walek.bus.GetWorkerEvent;
import com.mal.a7walek.models.Job;
import com.mal.a7walek.models.User;
import com.mal.a7walek.models.Worker;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by OmarAli on 25/10/2016.
 */

public class FirebaseManager {

    private DatabaseReference mDatabase;
    private Bus mBus;

    public FirebaseManager() {
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void AddNewUser(User user) {
        mDatabase.child(Constants.ROOT_USERS).child(user.getToken()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //user added successfully
                mBus.post(new AddRecordEvent(true));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // error adding user
                mBus.post(new AddRecordEvent(false));

                Log.d("error : ", e.getMessage());
            }
        });
    }


    public void AddNewWorker(Worker worker) {
        mDatabase.child(Constants.ROOT_WORKERS).child(worker.getToken()).setValue(worker).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //user added successfully
                mBus.post(new AddRecordEvent(true));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // error adding user
                mBus.post(new AddRecordEvent(false));

                Log.d("error : ", e.getMessage());
            }
        });
    }


    public void AddNewJob(Job job) {
        String key = mDatabase.child(Constants.ROOT_JOBS).push().getKey();

        Map<String, Object> jobRef = new HashMap<>();
        jobRef.put(key, "true");


        Map<String, Object> childUpdates = new HashMap<>();

        //  /jobs/key
        childUpdates.put("/" + Constants.ROOT_JOBS + "/" + key + "/", job);

        //  /users/20120252/jobs
        childUpdates.put("/" + Constants.ROOT_USERS + "/" + job.getUser_token() + "/" + Constants.ROOT_JOBS + "/", jobRef);

        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mBus.post(new AddRecordEvent(true));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mBus.post(new AddRecordEvent(false));
            }
        });

    }


    public void getUser(final String userToken) {
        mDatabase.child(Constants.ROOT_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // check if user exists
                boolean isUserExists = dataSnapshot.hasChild(userToken);

                if (isUserExists) {
                    Query myTopPostsQuery = mDatabase.child(Constants.ROOT_USERS).child(userToken);

                    myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> response = (HashMap<String, Object>) dataSnapshot.getValue();
                            mBus.post(new GetUserEvent(new User().BuildUser(response)));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mBus.post(new ErrorEvent(databaseError.toString()));
                        }
                    });
                } else {
                    // user not found callback
                    mBus.post(new GetUserEvent(false));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });
    }


    public void getWorker(final String workerToken) {
        mDatabase.child(Constants.ROOT_WORKERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // check if user exists
                boolean isUserExists = dataSnapshot.hasChild(workerToken);

                if (isUserExists) {
                    Query myTopPostsQuery = mDatabase.child(Constants.ROOT_WORKERS).child(workerToken);

                    myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Worker worker = dataSnapshot.getValue(Worker.class);
                            mBus.post(new GetWorkerEvent(worker));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mBus.post(new ErrorEvent(databaseError.toString()));
                        }
                    });
                } else {
                    // user not found callback
                    mBus.post(new GetWorkerEvent(false));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });
    }


    public ArrayList<Job> getAllJobs() {

        final ArrayList<Job>userJobs = new ArrayList<>();

        Query myTopPostsQuery = mDatabase.child(Constants.ROOT_JOBS);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String,Object>jobs = (HashMap<String, Object>) dataSnapshot.getValue();
                Iterator it = jobs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    Job userJob = new Job();
                    userJob.BuildJob((HashMap<String,Object>)pair.getValue());
                    userJob.setKey(pair.getKey().toString());

                    userJobs.add(userJob);

                    it.remove(); // avoids a ConcurrentModificationException
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });

        return userJobs;
    }


}
