package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.mal.a7walek.R;
import com.mal.a7walek.models.Worker;

public class WorkerRequestDetails extends AppCompatActivity {

    private ImageView clientImage, requestImage;
    private TextView clientName, clientAddress, clientDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_request_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clientImage = (ImageView) findViewById(R.id.detailed_client_image);
        requestImage = (ImageView) findViewById(R.id.detailed_request_image);
        clientName = (TextView) findViewById(R.id.detailed_client_name);
        clientAddress = (TextView) findViewById(R.id.detailed_client_address);
        clientDescription = (TextView) findViewById(R.id.detailed_client_description);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), WorkerOfferHelp.class));
            }
        });
    }

}
