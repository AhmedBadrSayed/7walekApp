package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.mal.a7walek.R;
import com.mal.a7walek.data.PrefManager;

public class UserType extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnWorker,btnHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        setViews();
    }

    public void setViews(){
        btnWorker = (ImageButton) findViewById(R.id.worker_login);
        btnHolder = (ImageButton) findViewById(R.id.user_login);
        btnWorker.setOnClickListener(this);
        btnHolder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.user_login:
                if(isCompletedProfile("client"))
                    startActivity(new Intent(UserType.this,ClientHome.class) );
                else
                    startActivity(new Intent(UserType.this, ClientCompleteProfile.class));

                break;


            case R.id.worker_login:
                if(isCompletedProfile("worker"))
                    startActivity(new Intent(UserType.this,WorkerHome.class) );
                else
                    startActivity(new Intent(UserType.this, WorkerCompleteProfile.class));

                break;
        }
    }


    /**
     * check if user completed his profile by checking the phone value in shared preference
     *
     * @param TAG
     * @return
     */
    private boolean isCompletedProfile(String TAG){
        if(TAG.equals("client"))
            return PrefManager.getStringValue(this,getString(R.string.pref_client_phone),null) != null;

        return PrefManager.getStringValue(this,getString(R.string.pref_worker_phone),null) != null;

    }
}
