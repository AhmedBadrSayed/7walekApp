package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.data.PrefManager;

public class UserType extends AppCompatActivity implements View.OnClickListener {

    TextView workerTV, userTV, userNameTV;
    ImageView profilePicIV;
    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        setViews();

//        Intent intent = this.getIntent();
//        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
//            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
//            DetailsArray = UserInfo.split("!");
//            UserName = DetailsArray[0];
//            ProfilePic = DetailsArray[1];
//        }
//
//        userNameTV.setText(UserName);
//        //feh moshkela fil picasso
//        Picasso.with(this).load(ProfilePic).into(profilePicIV);
    }

    public void setViews(){
        workerTV = (TextView) findViewById(R.id.worker_login);
        userTV = (TextView) findViewById(R.id.user_login);
        userNameTV = (TextView) findViewById(R.id.user_name);
        profilePicIV = (ImageView) findViewById(R.id.profile_pic);
        workerTV.setOnClickListener(this);
        userTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_login:
                if(!PrefManager.getStringValue(this,getString(R.string.pref_my_phone),"").equals("")){
                    startActivity(new Intent(UserType.this,ClientHome.class) );
                }else {
                    Intent intent = new Intent(UserType.this, ClientCompleteProfile.class);
                //    intent.putExtra(Intent.EXTRA_TEXT, UserName + "!" + ProfilePic);
                    startActivity(intent);
                }
                break;

            case R.id.worker_login:
                if(!PrefManager.getStringValue(this,getString(R.string.pref_my_profession),"").equals("")){
                    startActivity(new Intent(UserType.this,WorkerHome.class) );
                }else {
                    Intent intent1 = new Intent(UserType.this, WorkerCompleteProfile.class);
               //     intent1.putExtra(Intent.EXTRA_TEXT, UserName + "!" + ProfilePic);
                    startActivity(intent1);
                }
                break;
        }
    }
}
