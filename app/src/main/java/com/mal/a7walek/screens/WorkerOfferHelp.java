package com.mal.a7walek.screens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.mal.a7walek.R;

public class WorkerOfferHelp extends AppCompatActivity {

    EditText offeredPrice,Comment;
    Button Done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_offer_help);

        offeredPrice = (EditText)findViewById(R.id.worker_offer_price);
        Comment = (EditText)findViewById(R.id.worker_offer_description);
        Done = (Button)findViewById(R.id.worker_offer_done);
    }
}
