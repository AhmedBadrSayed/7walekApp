package com.mal.a7walek.utility;

import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.ErrorEvent;
import com.mal.a7walek.bus.GetAllCommentsEvent;
import com.mal.a7walek.bus.GetAllJobsEvent;
import com.mal.a7walek.bus.GetUserEvent;
import com.mal.a7walek.bus.GetUserJobsEvent;
import com.mal.a7walek.bus.GetWorkerEvent;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.models.Comment;
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
    private FirebaseStorage mFirebaseStorage;
    private Bus mBus;
    ArrayList<Comment> workerComments = new ArrayList<>();

    public FirebaseManager() {
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
    }


    public void AddKey(String hash){
        mDatabase.child("key").setValue(hash);
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


    public void AddNewJob(final Job job) {
        final String newJobKey = mDatabase.child(Constants.ROOT_JOBS).push().getKey();

        final Map<String, Object> childUpdates = new HashMap<>();

        //  /jobs/key
        childUpdates.put("/" + Constants.ROOT_JOBS + "/" + newJobKey + "/", job);

        mDatabase.child(Constants.ROOT_USERS).child(job.getUser_token()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = new User();
                user.BuildUser((HashMap<String,Object>)dataSnapshot.getValue());

                if(user.getJobReference()!=null){

                    Query myTopPostsQuery =  mDatabase.child(Constants.ROOT_USERS).child(job.getUser_token()).child(Constants.ROOT_JOBS);
                    myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,Object>jobRef= (HashMap<String,Object>)dataSnapshot.getValue();

                            if(jobRef!=null){
                                jobRef.put(newJobKey,"true");

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

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    HashMap<String,Object>jobRef= new HashMap<String, Object>();
                    jobRef.put(newJobKey,"true");
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void AddNewComment(Comment comment){
        String key = mDatabase.child(Constants.ROOT_COMMENTS).push().getKey();

        Map<String, Object> jobRef = new HashMap<>();
        jobRef.put(key, "true");


        Map<String, Object> childUpdates = new HashMap<>();

        //  /comments/key
        childUpdates.put("/" + Constants.ROOT_COMMENTS + "/" + key + "/", comment);

        //  /jobs/kilmmjskl556sg/comments
        childUpdates.put("/" + Constants.ROOT_JOBS + "/" + comment.getJobToken()+ "/" + Constants.ROOT_COMMENTS + "/", jobRef);

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


    public void getWorkerNearByJobs() {

        final ArrayList<Job>allJobs = new ArrayList<>();

        Query myTopPostsQuery = mDatabase.child(Constants.ROOT_JOBS);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String,Object>jobs = (HashMap<String, Object>) dataSnapshot.getValue();
                if(jobs!=null){
                    Iterator it = jobs.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();

                        Job userJob = new Job();
                        userJob.BuildJob((HashMap<String,Object>)pair.getValue());
                        userJob.setKey(pair.getKey().toString());

                        allJobs.add(userJob);
                        if(allJobs.size()==jobs.size())
                            mBus.post(new GetAllJobsEvent(allJobs));

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });

    }


    public void getUserJobs(String userToken){
        final ArrayList<Job>userJobs = new ArrayList<>();

        mDatabase.child(Constants.ROOT_USERS).child(userToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User();
                user.BuildUser((HashMap<String,Object>)dataSnapshot.getValue());

                if(user.getJobReference()!=null) {
                    final Map<String,Object>jobs = user.getJobReference();

                    Iterator it = jobs.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry pair = (Map.Entry)it.next();
                        final String jobKey = pair.getKey().toString();

                        Query jobQuery = mDatabase.child(Constants.ROOT_JOBS).child(jobKey);

                        jobQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Job job = dataSnapshot.getValue(Job.class);
                                job.setKey(jobKey);

                                userJobs.add(job);

                                if(userJobs.size()==jobs.size())
                                    mBus.post(new GetUserJobsEvent(userJobs));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mBus.post(new ErrorEvent(databaseError.toString()));
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });

    }


    public void getJobComments(String jobKey){

        final Query myTopPostsQuery = mDatabase.child(Constants.ROOT_JOBS).child(jobKey).child(Constants.ROOT_COMMENTS);

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final HashMap<String,Object>comments = (HashMap<String, Object>) dataSnapshot.getValue();

                if(comments!=null){

                    Iterator it = comments.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();

                        Query commentQuery = mDatabase.child(Constants.ROOT_COMMENTS).child(pair.getKey().toString());
                        commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Comment comment = dataSnapshot.getValue(Comment.class);

                                Query workerQuery = mDatabase.child(Constants.ROOT_WORKERS).child(comment.getWorkerToken());
                                workerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        comment.setWorker(dataSnapshot.getValue(Worker.class));
                                        workerComments.add(comment);
                                        if(workerComments.size()==comments.size()){
                                            mBus.post(new GetAllCommentsEvent(workerComments));
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        mBus.post(new ErrorEvent(databaseError.toString()));
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mBus.post(new ErrorEvent(databaseError.toString()));
                            }
                        });

                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBus.post(new ErrorEvent(databaseError.toString()));
            }
        });

    }


    public void updateTokenFCM(String userID , String newToken){

        mDatabase.child(Constants.ROOT_USERS).child(userID).child("token").setValue(newToken).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    public void uploadPhoto(byte[]data , String imageName){
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl(Constants.STORAGE_BUCKET);
        StorageReference imageRef = storageRef.child("/images/"+imageName+".jpg");

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mBus.post(new UploadImageEvent(downloadUrl.toString()));
            }
        });
    }


}
