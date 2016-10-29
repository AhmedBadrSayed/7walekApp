package com.mal.a7walek.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Job;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientAddRequest extends AppCompatActivity {

    @BindView(R.id.request_upload_image)ImageView requestImage;
    @BindView(R.id.request_add_category)TextView requestCategory;
    @BindView(R.id.request_add_desc)TextView requestDescription;
    @BindView(R.id.upload)ImageButton upload;
    @BindView(R.id.btn_publish_request)Button publishMyRequest;

    Bus mBus;
    FirebaseManager mFirebaseManager;

    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        ButterKnife.bind(this);

        mBus = BusProvider.getInstance();

        mFirebaseManager = new FirebaseManager();


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        publishMyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadRequestImage();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            requestImage.setImageBitmap(photo);
        }
    }


    /**********************************************************************************************************/


    public void uploadRequestImage(){
        // Get the data from an ImageView as bytes
        requestImage.setDrawingCacheEnabled(true);
        requestImage.buildDrawingCache();
        Bitmap bitmap = requestImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        mFirebaseManager.uploadPhoto(data,getReandomNumber());
    }


    @Subscribe
    public void OnUserAddedEvent(AddRecordEvent addRecordEvent){

        if(addRecordEvent.isSuccess())
            //user added successfully
            startActivity(new Intent(this,ClientHome.class));
        else
            //error adding user
            Log.d("user added : ","false");

    }

    @Subscribe
    public void OnUploadComplete(UploadImageEvent uploadEvent){
        saveMyRequest(uploadEvent.getUrl());
    }


    private void saveMyRequest(String imageURL){

        String category = requestCategory.getText().toString();
        String description = requestDescription.getText().toString();
        String userKey = PrefManager.getStringValue(this,getString(R.string.pref_my_phone),"");
        String address = PrefManager.getStringValue(this,getString(R.string.pref_my_address),"");
        double lat = PrefManager.getFloatValue(this,getString(R.string.pref_my_lat),0);
        double lng = PrefManager.getFloatValue(this,getString(R.string.pref_my_lng),0);

        mFirebaseManager.AddNewJob(new Job(imageURL,description,category,userKey,address,lat,lng));
    }


    public String getReandomNumber(){
        Random rn = new Random();
        int n = 150 - 0 + 1;
        int i = rn.nextInt() % n;
        return((0 + i)+"");
    }

}
