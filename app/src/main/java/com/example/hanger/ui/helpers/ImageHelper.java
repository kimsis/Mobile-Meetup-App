package com.example.hanger.ui.helpers;

import static com.google.firebase.messaging.Constants.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ImageHelper {

    // instance for firebase storage and StorageReference
    StorageReference storageReference;
    Context context;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener;
    OnFailureListener onFailureListener;
    OnProgressListener<UploadTask.TaskSnapshot> onProgressListener;

    public ImageHelper(Context context) {
        this.context = context;
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance("gs://hanger-1648c.appspot.com").getReference();
        this.auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        onSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(
                    UploadTask.TaskSnapshot taskSnapshot) {

                // Image uploaded successfully
                // Dismiss dialog
                progressDialog.dismiss();
                Toast
                        .makeText(context,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        };
        onFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast
                        .makeText(context,
                                "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        };
        onProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {

                    // Progress Listener for loading
                    // percentage on the dialog box
                    @Override
                    public void onProgress(
                            UploadTask.TaskSnapshot taskSnapshot) {
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(
                                "Uploaded "
                                        + (int) progress + "%");
                    }
                };

    }

    public void uploadImage(Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("images/" + auth.getUid() + ".jpg");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)
                    .addOnProgressListener(onProgressListener);
        }
    }

    public void getImage(ImageView imageView) {
        StorageReference ref = storageReference .child("images/" + auth.getUid() + ".jpg");
        GlideApp.with(context)
                .load(ref)
                .into(imageView);
    }
}
