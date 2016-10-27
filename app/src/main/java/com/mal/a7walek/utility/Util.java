package com.mal.a7walek.utility;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

//import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by OmarAli on 31/07/2016.
 */
public class Util {

    /*public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void sweetErrorMessage(Context ctx , String errorMessage) {

        new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(errorMessage)
                .show();
    }

    public static void ViewToast(Context ctx , String text){
        Toast.makeText( ctx,
                        text,
                        Toast.LENGTH_SHORT
                      ).show();
    }

    public static void SnackbarMessage(View view , String text){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }



    public static void loadImage(final Context ctx , final ImageView imageView , final String url){
        Picasso.with(ctx)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(ctx)
                                .load(url)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso","Could not fetch image");
                                    }
                                });
                    }
                });
    }*/

}
