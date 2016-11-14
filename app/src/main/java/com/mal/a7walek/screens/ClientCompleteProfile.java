package com.mal.a7walek.screens;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mal.a7walek.R;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.User;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientCompleteProfile extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.image_profile)
    ImageView userImage;
    @BindView(R.id.tv_name)
    EditText userName;
    @BindView(R.id.et_address)
    EditText userAddress;
    @BindView(R.id.et_phone_number)
    EditText phoneNumber;
    @BindView(R.id.image_location)
    ImageView image_location;
   // @BindView(R.id.avl_loading)AVLoadingIndicatorView avl_loading;
    @BindView(R.id.upload)ImageButton upload;

    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;
    FirebaseManager mFirebaseManager;

    ProgressDialog barProgressDialog;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    private static final int SELECT_PHOTO = 100;

    String TAG = ClientCompleteProfile.this.getClass().getSimpleName();
    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complete_profile);

        ButterKnife.bind(this);

        mFirebaseManager = new FirebaseManager();

        mBus = BusProvider.getInstance();

      //  getExtras_And_PrepareViews();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.image_location:

                break;
        }
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
                        userImage.setImageBitmap(image);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }


    public void launchProgress(){
        barProgressDialog = new ProgressDialog(ClientCompleteProfile.this);

        barProgressDialog.setTitle(getString(R.string.msg_dialog_title));
        barProgressDialog.setMessage(getString(R.string.msg_dialog_title));
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.show();
    }

    /****************************************************************************************************************/



    public void getExtras_And_PrepareViews() {
        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
            DetailsArray = UserInfo.split("!");
            UserName = DetailsArray[0];
            ProfilePic = DetailsArray[1];

            userName.setText(UserName);
            Picasso.with(this).load(ProfilePic).into(userImage);
        }
    }



    /**
     * save user profile
     *
     * @param view
     */
    public void saveProfile(View view) {

        launchProgress();

    //    avl_loading.setVisibility(View.VISIBLE);
    //    avl_loading.show();

        //save user info in shared pref
        saveInfoInSharedPref();

        uploadPhoto();
        //add user to firebase database

    }


    public void uploadPhoto(){
        // Get the data from an ImageView as bytes
        userImage.setDrawingCacheEnabled(true);
        userImage.buildDrawingCache();
        Bitmap bitmap = userImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        mFirebaseManager.uploadPhoto(data,getReandomNumber());
    }


    public String getReandomNumber(){
        Random rn = new Random();
        int n = 210 - 151 + 1;
        int i = rn.nextInt() % n;
        return((151 + i)+"");
    }


    public void saveInfoInSharedPref(){
        PrefManager.saveStringValue(this,getString(R.string.pref_my_address),userAddress.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_my_name),userName.getText().toString());

        PrefManager.saveFloatValue(this,getString(R.string.pref_my_lat),(float)2.11);
        PrefManager.saveFloatValue(this,getString(R.string.pref_my_lng),(float)98.55);
    }


    /**
     * add user to firebase database
     *
     * and receive callback with failed OR success insert
     *
     */
    private void saveUserToFirebase(String ProfilePic){
        User user = new User(userName.getText().toString(),phoneNumber.getText().toString(),ProfilePic
                ,userAddress.getText().toString(),2.11,98.55,phoneNumber.getText().toString());

        mFirebaseManager.AddNewUser(user);
    }




    @Subscribe
    public void OnUploadComplete(UploadImageEvent uploadEvent){
        Log.d("Image URL : " , uploadEvent.getUrl());
        PrefManager.saveStringValue(this,getString(R.string.pref_my_photo),uploadEvent.getUrl());
        saveUserToFirebase(uploadEvent.getUrl());
    }


    /**
     * callback that will be fired when user added , check if adding is success or not
     *
     * @param addRecordEvent
     */
    @Subscribe
    public void OnUserAddedEvent(AddRecordEvent addRecordEvent){

        // save user_key which is his phone number in shared preference to be used in future calls
        PrefManager.saveStringValue(this,getString(R.string.pref_my_phone),phoneNumber.getText().toString());


        if(addRecordEvent.isSuccess()){

           // avl_loading.hide();
            barProgressDialog.hide();
            //user added successfully
            startActivity(new Intent(this,ClientHome.class));
            Log.d("user added : ","true");
        }
        else{
            //error adding user
            Log.d("user added : ","false");
        }

    }

    /********************************  Google Location API Overrided Methods  *********************************/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            userAddress.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }
}
