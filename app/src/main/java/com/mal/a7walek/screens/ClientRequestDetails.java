package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mal.a7walek.R;
import com.mal.a7walek.adapters.ClientRequestDetailsAdapter;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.GetAllCommentsEvent;
import com.mal.a7walek.models.Comment;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ClientRequestDetails extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ClientRequestDetailsAdapter clientRequestDetailsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Comment> requestsList = new ArrayList<>();
    private static String LOG_TAG = "CardViewActivity";
    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_request_details);

        mBus = BusProvider.getInstance();

        getWorkersComments();

        setupRecycleView();

        clientRequestDetailsAdapter.setOnItemClickListener(new ClientRequestDetailsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startActivity(new Intent(getApplication(), WorkerDetails.class));
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    /**********************************************************************************************************/


    private void setupRecycleView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.client_request_details_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        clientRequestDetailsAdapter = new ClientRequestDetailsAdapter(this,requestsList);
        mRecyclerView.setAdapter(clientRequestDetailsAdapter);
    }


    public void getWorkersComments(){
        if(getIntent()!=null){
            String job_key = getIntent().getStringExtra(getString(R.string.pref_extra_job_key));
            FirebaseManager mFirebaseManager = new FirebaseManager();
            mFirebaseManager.getJobComments(job_key);
        }

    }


    /**
     *callback received when getting comments on client job
     *
     * @param commentsEvent
     */
    @Subscribe
    public void OnGetJobComments(GetAllCommentsEvent commentsEvent){
        if(commentsEvent.getWorkerComments()!=null && commentsEvent.getWorkerComments().size()!=0){
            requestsList.addAll(commentsEvent.getWorkerComments());
            clientRequestDetailsAdapter.notifyDataSetChanged();
        }
    }

}
