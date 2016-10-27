package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.utility.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserCompleteProfile extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.image_profile)
    ImageView userImage;
    @BindView(R.id.tv_name)
    TextView userName;
    @BindView(R.id.et_address)
    EditText userAddress;
    @BindView(R.id.et_phone_number)
    EditText phoneNumber;
    @BindView(R.id.image_location)
    ImageView image_location;
    @BindView(R.id.btn_save)
    Button btn_save;
    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;

    String TAG = UserCompleteProfile.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complete_profile);

        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
            DetailsArray = UserInfo.split("!");
            UserName = DetailsArray[0];
            ProfilePic = DetailsArray[1];
        }

        userName.setText(UserName);
        Picasso.with(this).load(ProfilePic).into(userImage);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.image_location:

                break;
        }
    }


    /**
     * save user profile
     *
     * @param view
     */
    public void saveProfile(View view) {
        startActivity(new Intent(this,UserProfile.class));
    }


    private void logMessage(String msg){
        Log.d(TAG , msg);
    }
}
