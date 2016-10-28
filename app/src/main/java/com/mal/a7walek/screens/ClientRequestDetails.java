package com.mal.a7walek.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mal.a7walek.DataObjects.ClientRequest;
import com.mal.a7walek.DataObjects.ClientRequestsDetails;
import com.mal.a7walek.R;
import com.mal.a7walek.adapters.ClientRequestDetailsAdapter;
import com.mal.a7walek.adapters.ClientRequestsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClientRequestDetails extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ClientRequestDetailsAdapter clientRequestDetailsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ClientRequestsDetails> requestsList;
    private static String LOG_TAG = "CardViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_request_details);

        initializeData();
        mRecyclerView = (RecyclerView)findViewById(R.id.client_request_details_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        clientRequestDetailsAdapter = new ClientRequestDetailsAdapter(requestsList);
        mRecyclerView.setAdapter(clientRequestDetailsAdapter);

        clientRequestDetailsAdapter.setOnItemClickListener(new ClientRequestDetailsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startActivity(new Intent(getApplication(), WorkerDetails.class));
            }
        });

    }

    private void initializeData() {
        requestsList = new ArrayList<>();
        requestsList.add(0,new ClientRequestsDetails(getString(R.string.dummy_name),getString(R.string.request_desc),R.mipmap.ic_launcher,150.00));
        requestsList.add(1,new ClientRequestsDetails(getString(R.string.dummy_name),getString(R.string.request_desc),R.mipmap.ic_launcher,150.00));
        requestsList.add(2,new ClientRequestsDetails(getString(R.string.dummy_name),getString(R.string.request_desc),R.mipmap.ic_launcher,150.00));
        requestsList.add(3,new ClientRequestsDetails(getString(R.string.dummy_name),getString(R.string.request_desc),R.mipmap.ic_launcher,150.00));
        requestsList.add(4,new ClientRequestsDetails(getString(R.string.dummy_name),getString(R.string.request_desc),R.mipmap.ic_launcher,150.00));
    }
}
