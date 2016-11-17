package com.mal.a7walek.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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


public class ClientCompleteProfile extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.image_profile)
    ImageView iv_userImage;
    @BindView(R.id.tv_name)
    TextView txt_userName;
    @BindView(R.id.et_address)
    EditText et_userAddress;
    @BindView(R.id.et_phone_number)
    EditText et_phoneNumber;

    ProgressDialog mProgressDialog;

    String clientName,clientPhoto;

    String TAG = ClientCompleteProfile.this.getClass().getSimpleName();

    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complete_profile);

        ButterKnife.bind(this);

        mBus = BusProvider.getInstance();

        getExtras_And_PrepareViews();

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

        mProgressDialog.show();

        //save user info in shared pref
        saveInfoInSharedPref();

        //add user to firebase database
        saveUserToFirebase();
    }


    public void saveInfoInSharedPref(){
        PrefManager.saveStringValue(this,getString(R.string.pref_client_address), et_userAddress.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_client_name),clientName);
        PrefManager.saveStringValue(this,getString(R.string.pref_client_photo),clientPhoto);
        PrefManager.saveFloatValue(this,getString(R.string.pref_client_lat),(float)2.11);
        PrefManager.saveFloatValue(this,getString(R.string.pref_client_lng),(float)98.55);
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
                , 2.11,98.55
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




}
