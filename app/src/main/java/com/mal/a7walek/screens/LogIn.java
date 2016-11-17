package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mal.a7walek.R;
import com.mal.a7walek.data.PrefManager;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LogIn extends AppCompatActivity {

    private static final String TAG = "LogIn";
    ImageButton fbloginButton;
    ProfileTracker profTrack;
    AccessTokenTracker accessTokenTracker;

    //Facebook login variables
    CallbackManager mFacebookCallbackManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_log_in);

        //initialize facebook login
        loginWithFacebook();

        fbloginButton = (ImageButton) findViewById(R.id.fb_login_button);

        fbloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LogIn.this, Arrays.asList("email","public_profile"));
            }
        });
    }

    /**
     * initialize facebook login
     */
    private void loginWithFacebook() {
        mFacebookCallbackManger = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManger, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                final String token = accessToken.getToken();
                int expire = (int) (accessToken.getExpires().getTime() / 1000);

                saveUserToken(token);

                Log.d("Facebook token", token);
                Log.d("Facebook token expires", "" + expire);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        if(bFacebookData!=null){

                            String userName = bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name");
                            String profile_pic = bFacebookData.getString("profile_pic");
                          //  user.setEmail(bFacebookData.getString("email"));

                            saveUserData(userName , profile_pic);

                            // start User Type screen and send user object as Extra ( Parcelable object )
                            startActivity(new Intent(LogIn.this,UserType.class));

                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Login Error " + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserData(String userName, String profile_pic) {
        //save client info
        PrefManager.saveStringValue(this,getString(R.string.pref_client_name),userName);
        PrefManager.saveStringValue(this,getString(R.string.pref_client_photo),profile_pic);

        //save worker info
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_name), userName);
        PrefManager.saveStringValue(this, getString(R.string.pref_worker_photo), profile_pic);
    }


    private void saveUserToken(String token){
        PrefManager.saveStringValue(this,getString(R.string.pref_access_token),token);
    }

    /**
     * extract data from facebook response
     * @param object
     * @return
     */
    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=300&height=200");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

            return bundle;
        }catch (Exception e){
            e.printStackTrace();
        }
        return bundle;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //call facebook callback
        mFacebookCallbackManger.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            accessTokenTracker.stopTracking();
            profTrack.stopTracking();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


}
