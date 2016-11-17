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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mal.a7walek.R;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Worker;
import com.mal.a7walek.utility.FirebaseManager;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkerCompleteProfile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindString(R.string.err_name)String err_name;
    @BindString(R.string.err_profession)String err_profession;
    @BindString(R.string.err_exp)String err_exp;
    @BindString(R.string.err_tel)String err_tel;

    @BindView(R.id.image_profile)
    ImageView workerImage;

    @NotEmpty (message = "error name" )
    @BindView(R.id.tv_name)
    TextView workerName;

    @NotEmpty (message = "error address" )
    @BindView(R.id.et_address)
    EditText workerAddress;

    @NotEmpty (message = "error number" )
    @BindView(R.id.et_phone_number)
    EditText phoneNumber;

    @NotEmpty (message = "error profession" )
    @BindView(R.id.et_profession)
    EditText workerProfession;

    @NotEmpty
    @BindView(R.id.et_years_of_exp)
    EditText workerExp;

    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.btn_upload_id)
    Button uploadNationalID;
    @BindView(R.id.upload)
    ImageButton upload;

    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;

    ProgressDialog barProgressDialog;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    byte[] byteArray;

    private static final int SELECT_PHOTO = 100;

    Bus mBus;
    FirebaseManager mFirebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_complete_profile);

        ButterKnife.bind(this);

        buildGoogleApiClient();

        mBus = BusProvider.getInstance();

        //  getExtras_And_PrepareViews();

        mFirebaseManager = new FirebaseManager();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });


//        Intent intent = this.getIntent();
//        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
//            UserInfo = intent.getStringExtra(Intent.EXTRA_TEXT);
//            DetailsArray = UserInfo.split("!");
//            UserName = DetailsArray[0];
//            ProfilePic = DetailsArray[1];
//        }
//
//        workerName.setText(UserName);
//        Picasso.with(this).load(ProfilePic).into(workerImage);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    /****************************************************************************************************************/


    public void launchProgress() {
        barProgressDialog = new ProgressDialog(WorkerCompleteProfile.this);

        barProgressDialog.setTitle(getString(R.string.msg_dialog_title));
        barProgressDialog.setMessage(getString(R.string.msg_dialog_title));
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.show();
    }


    public void getExtras_And_PrepareViews() {
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
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
    public void uploadNationalID(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap image = BitmapFactory.decodeStream(imageStream);
                        workerImage.setImageBitmap(image);

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
    public void saveProfile(View view) {


            launchProgress();

            //save user info in shared pref
            saveInfoInSharedPref();

            //add user to firebase database
            uploadPhoto();



    }


    public void uploadPhoto() {
        // Get the data from an ImageView as bytes
        workerImage.setDrawingCacheEnabled(true);
        workerImage.buildDrawingCache();
        Bitmap bitmap = workerImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        mFirebaseManager.uploadPhoto(data, getReandomNumber());
    }


    public String getReandomNumber() {
        Random rn = new Random();
        int n = 500 - 220 + 1;
        int i = rn.nextInt() % n;
        return ((220 + i) + "");
    }


    public void saveInfoInSharedPref() {
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_address), workerAddress.getText().toString());
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_name), workerName.getText().toString());
        PrefManager.saveStringValue(this, getString(R.string.pref_my_profession), workerProfession.getText().toString());
        PrefManager.saveFloatValue(this, getString(R.string.pref_worker_lat), (float) 2.11);
        PrefManager.saveFloatValue(this, getString(R.string.pref_worker_lng), (float) 98.55);
    }


    /**
     * add user to firebase database
     *
     * and receive callback with failed OR success insert
     *
     */
    private void saveWorkerToFirebase(String ProfilePic) {
        Worker worker = new Worker(workerName.getText().toString(), workerName.getText().toString(), ProfilePic
                , workerAddress.getText().toString(), 2.11, 98.55, phoneNumber.getText().toString(), "id_url", workerProfession.getText().toString());

        mFirebaseManager.AddNewWorker(worker);
    }


    @Subscribe
    public void OnUploadComplete(UploadImageEvent uploadEvent) {
        Log.d("Image URL : ", uploadEvent.getUrl());
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_photo), uploadEvent.getUrl());
        saveWorkerToFirebase(uploadEvent.getUrl());
    }


    /**
     * callback that will be fired when user added , check if adding is success or not
     *
     * @param addRecordEvent
     */
    @Subscribe
    public void OnWorkerAddedEvent(AddRecordEvent addRecordEvent) {

        if (addRecordEvent.isSuccess()) {
            //user added successfully
            barProgressDialog.hide();
            startActivity(new Intent(this, WorkerHome.class));
        } else {
            //error adding user
            Log.d("user added : ", "false");
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
            workerAddress.setText(address);
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