package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mal.a7walek.R;

/**
 * Created by Ahmed Badr on 28/10/2016.
 */
public class WorkerDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
