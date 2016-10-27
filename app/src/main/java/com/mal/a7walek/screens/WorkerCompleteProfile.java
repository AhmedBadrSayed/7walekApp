package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkerCompleteProfile extends AppCompatActivity {

    @BindView(R.id.image_profile)ImageView workerImage;
    @BindView(R.id.tv_name)TextView workerName;
    @BindView(R.id.et_address)EditText workerAddress;
    @BindView(R.id.et_phone_number)EditText phoneNumber;
    @BindView(R.id.et_profession)EditText workerProfession;
    @BindView(R.id.et_years_of_exp)EditText workerExp;
    @BindView(R.id.btn_save)Button btn_save;
    @BindView(R.id.btn_upload_id)Button uploadNationalID;
    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_complete_profile);

        ButterKnife.bind(this);

        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
            DetailsArray = UserInfo.split("!");
            UserName = DetailsArray[0];
            ProfilePic = DetailsArray[1];
        }

        workerName.setText(UserName);
        Picasso.with(this).load(ProfilePic).into(workerImage);

    }

    /**
     * choose worker id from gallery to upload
     *
     * @param view
     */
    public void uploadNationalID(View view){

    }


    /**
     * save worker profile
     *
     * @param view
     */
    public void saveProfile(View view){

    }
}
