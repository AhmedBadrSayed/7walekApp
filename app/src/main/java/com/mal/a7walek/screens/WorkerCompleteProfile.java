package com.mal.a7walek.screens;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mal.a7walek.R;
import com.mal.a7walek.bus.AddRecordEvent;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.UploadImageEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.User;
import com.mal.a7walek.models.Worker;
import com.mal.a7walek.utility.FirebaseManager;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mal.a7walek.screens.ClientCompleteProfile.checkGpsAv;

public class WorkerCompleteProfile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindString(R.string.err_name)String err_name;
    @BindString(R.string.err_profession)String err_profession;
    @BindString(R.string.err_exp)String err_exp;
    @BindString(R.string.err_tel)String err_tel;

    @BindView(R.id.image_profile)
    ImageView iv_workerImage;

    @NotEmpty (message = "error name" )
    @BindView(R.id.tv_name)
    TextView txt_workerName;

    @NotEmpty (message = "error address" )
    @BindView(R.id.et_address)
    EditText et_workerAddress;

    @NotEmpty (message = "error number" )
    @BindView(R.id.et_phone_number)
    EditText et_phoneNumber;

    @NotEmpty (message = "error profession" )
    @BindView(R.id.et_profession)
    EditText et_workerProfession;

    @NotEmpty
    @BindView(R.id.et_years_of_exp)
    EditText et_workerExp;

    @BindView(R.id.btn_save)
    Button btn_save;

    @BindView(R.id.btn_upload_id)
    Button uploadNationalID;

    ProgressDialog mProgressDialog;

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private AlertDialog mGpsDialog;
    User user;

    Bitmap mBitmapNational_id;

    private static final int SELECT_PHOTO = 100;
    String workerName,workerPhoto;
    Bus mBus;
    FirebaseManager mFirebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_complete_profile);

        ButterKnife.bind(this);

        mFirebaseManager = new FirebaseManager();
        buildGoogleApiClient();

        mBus = BusProvider.getInstance();

        getExtras_And_PrepareViews();

        checkGps();
    }


    private void checkGps() {

        if (!checkGpsAv(this)) {
            if (mGpsDialog == null) {
                mGpsDialog = buildAlertMessageNoGps();
            }
            if (mGpsDialog != null) {
                mGpsDialog.show();
            }
        }
    }

    private AlertDialog buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("تفعيل الموقع الذهاب اللى الاعدادات؟")
                .setCancelable(false)
                .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
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


    public void getExtras_And_PrepareViews() {

        workerName = PrefManager.getStringValue(this,getString(R.string.pref_client_name),null);
        workerPhoto = PrefManager.getStringValue(this,getString(R.string.pref_client_photo),null);

        if (txt_workerName !=null && workerPhoto!=null) {
            txt_workerName.setText(workerName);
            Picasso.with(this).load(workerPhoto).into(iv_workerImage);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.msg_dialog_wait));
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
                        mBitmapNational_id = BitmapFactory.decodeStream(imageStream);

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


        if(mLocation==null){
            Toast.makeText(this,"قم بتفعيل موقعك اولا",Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.show();

        //save user info in shared pref
        saveInfoInSharedPref();

        //add user to firebase database
        uploadPhoto();
    }


    /**
     * upload the select image fot national id to firebase
     *
     */
    public void uploadPhoto() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmapNational_id.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // upload user national id image to firebase storage
        // assign user name to that image  for example : ( omar_id.jpg )
        mFirebaseManager.uploadPhoto(data, txt_workerName.getText().toString()+"_id");
    }


    public void saveInfoInSharedPref() {
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_address), et_workerAddress.getText().toString());
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_name), workerName);
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_photo), workerPhoto);
        PrefManager.saveStringValue(this, getString(R.string.pref_my_profession), et_workerProfession.getText().toString());
        PrefManager.saveFloatValue(this, getString(R.string.pref_worker_lat), (float) mLocation.getLatitude());
        PrefManager.saveFloatValue(this, getString(R.string.pref_worker_lng), (float) mLocation.getLongitude());
    }


    /**
     * add user to firebase database
     *
     * and receive callback with failed OR success insert
     *
     */
    private void saveWorkerToFirebase(String nationalID_url) {

        Worker worker = new Worker(txt_workerName.getText().toString()
                , workerName
                , workerPhoto
                , et_workerAddress.getText().toString()
                , mLocation.getLatitude(), mLocation.getLongitude()
                , et_phoneNumber.getText().toString()
                , nationalID_url
                , et_workerProfession.getText().toString());

        mFirebaseManager.AddNewWorker(worker);
    }


    /**
     *
     * @param uploadEvent
     */
    @Subscribe
    public void OnUploadComplete(UploadImageEvent uploadEvent) {
        saveWorkerToFirebase(uploadEvent.getUrl());
    }


    /**
     * callback that will be fired when user added , check if adding is success or not
     *
     * @param addRecordEvent
     */
    @Subscribe
    public void OnWorkerAddedEvent(AddRecordEvent addRecordEvent) {

        mProgressDialog.hide();

        if (addRecordEvent.isSuccess()) {
            //user added successfully
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
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());
//
//        try {
//            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
//            String address = addresses.get(0).getAddressLine(0);
//            et_workerAddress.setText(address);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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