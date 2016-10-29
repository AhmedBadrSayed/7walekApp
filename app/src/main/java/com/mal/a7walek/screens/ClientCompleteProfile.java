package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientCompleteProfile extends AppCompatActivity implements View.OnClickListener{

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
    @BindView(R.id.avl_loading)AVLoadingIndicatorView avl_loading;

    String UserInfo, UserName, ProfilePic;
    String[] DetailsArray;

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

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount=0.0f;
        this.getWindow().setAttributes(lp);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);;

        avl_loading.setVisibility(View.VISIBLE);
        avl_loading.show();

        //save user info in shared pref
        saveInfoInSharedPref();

        //add user to firebase database
        saveUserToFirebase();
    }


    public void saveInfoInSharedPref(){
        PrefManager.saveStringValue(this,getString(R.string.pref_my_address),userAddress.getText().toString());
        PrefManager.saveStringValue(this,getString(R.string.pref_my_name),UserName);
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
    private void saveUserToFirebase(){
        User user = new User(UserName,phoneNumber.getText().toString(),ProfilePic
                ,userAddress.getText().toString(),2.11,98.55,phoneNumber.getText().toString());

        FirebaseManager mFirebaseManager = new FirebaseManager();
        mFirebaseManager.AddNewUser(user);
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

            avl_loading.hide();

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
