package com.maker.SnakeSnap;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import com.soundcloud.android.crop.Crop;

public class MainActivity extends AppCompatActivity {
    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;
    public static final int IMAGE_PICK_CODE = 1000;

    // will hold uri of image obtained from camera
    private Uri imageUri;

    //private boolean pick = false;

    // string to send to next activity that describes the chosen classifier
    private String chosen;

    private boolean quant;

    // button for each available classifier
    private Button take_picture;
    private Button uploadimage;
    private Button fasb;
    private Button snake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // request permission to use the camera on the user's phone
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        // request permission to write data (aka images) to the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        // request permission to read data (aka images) from the user's external storage of their phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        take_picture = (Button)findViewById(R.id.take_picture);
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // filename in assets
                chosen = "snake_model2.tflite";
                quant = false;
                // open camera
                openCameraIntent();
            }
        });

        uploadimage = (Button)findViewById(R.id.uploadimage);
        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // filename in assets
                chosen = "snake_model2.tflite";
                quant = false;
                // open gallery
                pickImageFromGalleryIntent();
            }
        });

        fasb = (Button)findViewById(R.id.fasb);
        fasb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this, ImageSliderActivity.class);
                startActivity(i);
            }
        });

        snake = (Button)findViewById(R.id.snake);
        snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SnakeInfo.class);
                startActivity(i);
            }
        });
    }

    // open gallery for user
    private void pickImageFromGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // opens camera for user
    private void openCameraIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        // tell camera where to store the resulting picture
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // start camera, and wait for it to finish
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
                System.exit(0);
            }
        }
    }

    // dictates what to do after the user takes an image, selects and image, or crops an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // if the camera activity is finished, obtained the uri, crop it to make it square, and send it to 'Classify' activity
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri source_uri = imageUri;
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                // need to crop it to square image as CNN's always required square input
                Crop.of(source_uri, dest_uri).asSquare().start(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            try{
                /// Get the Image from data
                Uri selectedImage = data.getData();
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Crop.of(selectedImage, dest_uri).asSquare().start(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // if cropping acitivty is finished, get the resulting cropped image uri and send it to 'Classify' activity
        if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            imageUri = Crop.getOutput(data);
            Intent i = new Intent(MainActivity.this, Classify.class);
            // put image data in extras to send
            i.putExtra("resID_uri", imageUri);
            // put filename in extras
            i.putExtra("chosen", chosen);
            // put model type in extras
            i.putExtra("quant", quant);
            // send other required data
            startActivity(i);
        }
    }
}
