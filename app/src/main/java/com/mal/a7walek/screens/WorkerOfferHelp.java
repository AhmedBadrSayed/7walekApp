package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mal.a7walek.R;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Comment;
import com.mal.a7walek.utility.FirebaseManager;

public class WorkerOfferHelp extends AppCompatActivity {

    EditText offeredPrice,txtComment;
    Button Done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_offer_help);

        final FirebaseManager mFirebaseManager = new FirebaseManager();

        offeredPrice = (EditText)findViewById(R.id.worker_offer_price);
        txtComment = (EditText)findViewById(R.id.worker_offer_description);
        Done = (Button)findViewById(R.id.worker_offer_done);

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String job_key = getIntent().getExtras().getString(getString(R.string.pref_extra_job_key));
                String worker_token = PrefManager.getStringValue(getApplicationContext(),getString(R.string.pref_my_name),"");
                Comment comment = new Comment(worker_token,job_key,txtComment.getText().toString(),offeredPrice.getText().toString());
                mFirebaseManager.AddNewComment(comment);

                startActivity(new Intent(WorkerOfferHelp.this,WorkerHome.class));
            }
        });
    }
}
