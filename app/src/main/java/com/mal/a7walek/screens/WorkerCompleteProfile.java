package com.mal.a7walek.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Worker;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    byte[] byteArray;

    private static final int SELECT_PHOTO = 100;

    Bus mBus;
    FirebaseManager mFirebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_complete_profile);

        ButterKnife.bind(this);

        mBus = BusProvider.getInstance();

        getExtras_And_PrepareViews();

        mFirebaseManager = new FirebaseManager();

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

    /****************************************************************************************************************/

    public void getExtras_And_PrepareViews() {
        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
            DetailsArray = UserInfo.split("!");
            UserName = DetailsArray[0];
            ProfilePic = DetailsArray[1];

            workerName.setText(UserName);
            Picasso.with(this).load(ProfilePic).into(workerImage);
        }
    }


    /**
     * choose worker id from gallery to upload
     *
     * @param view
     */
    public void uploadNationalID(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap image = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    /**
     * save worker profile
     *
     * @param view
     */
    public void saveProfile(View view){
        //save user info in shared pref
        saveInfoInSharedPref();

        //add user to firebase database
        saveWorkerToFirebase("id_url");
    }


    public void saveInfoInSharedPref(){
        PrefManager.saveStringValue(this,getString(R.string.pref_my_address),workerAddress.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_my_name),UserName);
        PrefManager.saveStringValue(this,getString(R.string.pref_my_profession),workerProfession.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_my_photo),ProfilePic);
        PrefManager.saveFloatValue(this,getString(R.string.pref_my_lat),(float)2.11);
        PrefManager.saveFloatValue(this,getString(R.string.pref_my_lng),(float)98.55);
    }


    /**
     * add user to firebase database
     *
     * and receive callback with failed OR success insert
     *
     */
    private void saveWorkerToFirebase(String id_url){
        Worker worker = new Worker(UserName,UserName,ProfilePic
                ,workerAddress.getText().toString(),2.11,98.55,phoneNumber.getText().toString(),id_url,workerProfession.getText().toString());


        mFirebaseManager.AddNewWorker(worker);
    }


    @Subscribe
    public void OnUploadComplete(UploadImageEvent uploadEvent){
        Log.d("Image URL : " , uploadEvent.getUrl());
        saveWorkerToFirebase(uploadEvent.getUrl());
    }


    /**
     * callback that will be fired when user added , check if adding is success or not
     *
     * @param addRecordEvent
     */
    @Subscribe
    public void OnWorkerAddedEvent(AddRecordEvent addRecordEvent){

        if(addRecordEvent.isSuccess())
            //user added successfully
            startActivity(new Intent(this,WorkerHome.class));
        else
            //error adding user
            Log.d("user added : ","false");

    }

}
