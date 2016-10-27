package com.mal.a7walek.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;

public class AddRequest extends AppCompatActivity {

    ImageView requestImage;
    ImageButton upload;
    TextView requestDescription, requestCategory;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        requestImage = (ImageView)findViewById(R.id.request_upload_image);
        requestCategory = (TextView)findViewById(R.id.request_add_category);
        requestDescription = (TextView)findViewById(R.id.request_add_desc);
        upload = (ImageButton)findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            requestImage.setImageBitmap(photo);
        }
    }
}
