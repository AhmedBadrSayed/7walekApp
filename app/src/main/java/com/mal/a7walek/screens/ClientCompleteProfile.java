package com.mal.a7walek.screens;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.User;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientCompleteProfile extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.image_profile)
    ImageView iv_userImage;
    @BindView(R.id.tv_name)
    TextView txt_userName;
    @BindView(R.id.et_address)
    EditText et_userAddress;
    @BindView(R.id.et_phone_number)
    EditText et_phoneNumber;

    ProgressDialog mProgressDialog;

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    String clientName,clientPhoto;
    private AlertDialog mGpsDialog;

    String TAG = ClientCompleteProfile.this.getClass().getSimpleName();

    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complete_profile);

        ButterKnife.bind(this);

        buildGoogleApiClient();

        mBus = BusProvider.getInstance();

        getExtras_And_PrepareViews();

        checkGps();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.image_location:

                break;
        }
    }


    /****************************************************************************************************************/

    public void getExtras_And_PrepareViews() {

        clientName = PrefManager.getStringValue(this,getString(R.string.pref_client_name),null);
        clientPhoto = PrefManager.getStringValue(this,getString(R.string.pref_client_photo),null);

        if (clientName!=null && clientPhoto!=null) {
            txt_userName.setText(clientName);
            Picasso.with(this).load(clientPhoto).into(iv_userImage);
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.msg_dialog_wait));

    }

    /**
     * save user profile
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
        saveUserToFirebase();
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

    public static boolean checkGpsAv(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public void saveInfoInSharedPref(){
        PrefManager.saveStringValue(this,getString(R.string.pref_client_address), et_userAddress.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_client_name),clientName);
        PrefManager.saveStringValue(this,getString(R.string.pref_client_photo),clientPhoto);
        PrefManager.saveFloatValue(this,getString(R.string.pref_client_lat),(float)mLocation.getLatitude());
        PrefManager.saveFloatValue(this,getString(R.string.pref_client_lng),(float)mLocation.getLongitude());
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


    /**
     * add user to firebase database
     *
     * and receive callback with failed OR success insert
     *
     */
    private void saveUserToFirebase(){
        User firebaseUser = new User( clientName
                , et_phoneNumber.getText().toString()
                , clientPhoto
                , et_userAddress.getText().toString()
                , mLocation.getLatitude(),mLocation.getLongitude()
                , et_phoneNumber.getText().toString());

        FirebaseManager mFirebaseManager = new FirebaseManager();

        mFirebaseManager.AddNewUser(firebaseUser);
    }


    /**
     * callback that will be fired when user added , check if adding is success or not
     *
     * @param addRecordEvent
     */
    @Subscribe
    public void OnUserAddedEvent(AddRecordEvent addRecordEvent){

        mProgressDialog.hide();

        // save user_key which is his phone number in shared preference to be used in future calls
        PrefManager.saveStringValue(this,getString(R.string.pref_client_phone), et_phoneNumber.getText().toString());

        if(addRecordEvent.isSuccess()){

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
