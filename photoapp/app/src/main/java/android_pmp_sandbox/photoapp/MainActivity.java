package android_pmp_sandbox.photoapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    File IMAGE_STORAGE_DIR;
    File CAPTURED_IMAGE_PHOTO;
    // File CAPTURED_IMAGE_METADATA;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    Uri mImageUri;
    Uri lastImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configure where images are to be stored
        IMAGE_STORAGE_DIR = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Configure where metadata are to be stored
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
            ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
            grabImage(imageView1, mImageUri);
            if (lastImageUri != null) {
                grabImage(imageView2, lastImageUri);
            }
        }   else {
            CAPTURED_IMAGE_PHOTO.delete();
            // CAPTURED_IMAGE_METADATA.delete();
        }
    }

    private void grabImage(ImageView imageView, Uri imgUri) {
        getContentResolver().notifyChange(imgUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imgUri);
            imageView.setImageBitmap(bitmap);
        }   catch (Exception e) {
            Log.e("ERR", "Failed to load image");
        }
    }

    public void takePhoto(View view) {
        if (!(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))) {
            // nothing
        }   else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                // File where photo should go
                File photoFile = null;

                /*
                try {
                    photoFile = createImageFile();
                }   catch (IOException e) {
                    e.printStackTrace();
                }
                */

                try {
                    CAPTURED_IMAGE_PHOTO = createImageFile();
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "android_pmp_sandbox.photoapp.fileprovider", CAPTURED_IMAGE_PHOTO);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }   catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File createImageFile() throws IOException {

        // Try to get last file to display in second window
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] dirFiles = dir.listFiles();

        // Cycle through files in directory
        if (dirFiles != null) {
            for (File child : dirFiles) {
                Log.d("File child", child.getAbsolutePath());
                lastImageUri = Uri.fromFile(child);
            }
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyMMddHHmmssSS").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mImageUri = Uri.fromFile(image);

        // Save file
        mCurrentPhotoPath = image.getAbsolutePath();
        String storDir = storageDir.getAbsolutePath();
        Log.d("storageDir", storDir);
        Log.d("mCurrentPhotoPath", mCurrentPhotoPath);
        Log.d("imageFileName", imageFileName);
        return image;
    }
}
