package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.GetUserEvent;
import com.mal.a7walek.models.User;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

public class WorkerRequestDetails extends AppCompatActivity {

    private ImageView clientImage, requestImage;
    private TextView clientName, clientAddress, clientDescription;
    FloatingActionButton fab;
    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_request_details);

        mBus = BusProvider.getInstance();

        setupViews();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String job_key = getIntent().getExtras().getString(getString(R.string.pref_extra_job_key));
                startActivity(new Intent(getApplication(), WorkerOfferHelp.class).putExtra(getString(R.string.pref_extra_job_key),job_key));
            }
        });

        if(getIntent()!=null){
            Bundle extras = getIntent().getExtras();
            clientDescription.setText(extras.getString("desc"));
            Picasso.with(this).load(extras.getString("iv")).into(requestImage);

            String client_token = getIntent().getStringExtra(getString(R.string.pref_client_token));
            FirebaseManager mFirebaseManager = new FirebaseManager();
            mFirebaseManager.getUser(client_token);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    /************************************************************************************************/



    private void setupViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        clientImage = (ImageView) findViewById(R.id.detailed_client_image);
        requestImage = (ImageView) findViewById(R.id.detailed_request_image);
        clientName = (TextView) findViewById(R.id.detailed_client_name);
        clientAddress = (TextView) findViewById(R.id.detailed_client_address);
        clientDescription = (TextView) findViewById(R.id.detailed_client_description);
        fab = (FloatingActionButton) findViewById(R.id.fab_worker_apply);
    }


    /**
     * callback that will be fired when requesting read data for user , if user not found then you
     * can insert new one ( in case of sign up , check if user exists first ,if not exist then add him )
     *
     * @param userEvent
     */
    @Subscribe
    public void OnGetUserEvent(GetUserEvent userEvent){
        User user = userEvent.getUser();
        clientName.setText(user.getUserName());
        clientAddress.setText(user.getAddress());
        Picasso.with(this).load(user.getImage_url()).into(clientImage);

    }

}
