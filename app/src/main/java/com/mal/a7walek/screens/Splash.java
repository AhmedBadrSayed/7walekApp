package com.mal.a7walek.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import com.mal.a7walek.R;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.utility.FirebaseManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mal.a7walek",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                FirebaseManager manager = new FirebaseManager();
                manager.AddKey( Base64.encodeToString(md.digest(), Base64.DEFAULT));

                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        printKeyHash(this);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    // if user logged in before then go to User Type screen
                    // else go to login screen
                    if(isUserLoggedIn()){
                        Intent start = new Intent(Splash.this, UserType.class);
                        startActivity(start);
                    }else{
                        Intent start = new Intent(Splash.this, LogIn.class);
                        startActivity(start);
                    }

                }
            }
        };
        timer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }


    /**
     * check if user logged in before
     *
     * @return
     */
    private boolean isUserLoggedIn(){
        if(PrefManager.getStringValue(this,getString(R.string.pref_access_token),null)!=null)
            return true;

        return false;
    }

    private  String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
