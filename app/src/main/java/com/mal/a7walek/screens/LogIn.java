package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mal.a7walek.R;

public class LogIn extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profTrack;
    AccessTokenTracker accessTokenTracker;
    Profile profile;
    String UserName, ProfilePic;
    Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_log_in);

        btnProfile = (Button) findViewById(R.id.profile);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this,UserType.class));
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("Token", loginResult.getAccessToken().getToken());

                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        Profile.fetchProfileForCurrentAccessToken();
                        AccessToken.setCurrentAccessToken(currentAccessToken);
                    }
                };
                accessTokenTracker.startTracking();

                if(Profile.getCurrentProfile() == null){
                    profTrack = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            UserName = currentProfile.getName();
                            ProfilePic = String.valueOf(currentProfile.getProfilePictureUri(100,100));
                            btnProfile.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(LogIn.this, UserType.class);
                            intent.putExtra(Intent.EXTRA_TEXT, UserName+"!"+ProfilePic);
                            startActivity(intent);
                        }
                    };
                }else {
                    profile = Profile.getCurrentProfile();
                    UserName = profile.getName();
                    ProfilePic = String.valueOf(profile.getProfilePictureUri(100,100));
                    Intent intent = new Intent(LogIn.this, UserType.class);
                    intent.putExtra(Intent.EXTRA_TEXT, UserName+"!"+ProfilePic);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancel() {
                Log.v("facebook - onCancel", "cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("facebook - onError", error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
