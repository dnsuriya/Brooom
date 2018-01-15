package com.sidedrive.brooom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class EditProfile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    Uri selectedImage;
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    ImageView imageView;
    //allow selected image to display in image view
    boolean loading = false;
    //    check image chaned or not
    boolean uploadImage = false;
    private TextView error_text;
    private FirebaseAuth mAuth;
    private EditText mobile;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        error_text = findViewById(R.id.text_error);
        mobile = findViewById(R.id.Mobile);

        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();

        mAuth = FirebaseAuth.getInstance();

        loadImage();

        getMobileEmail();

        uploadImage = false;
    }


    private void getMobileEmail(){
        final String user_id = mAuth.getCurrentUser().getUid();

        Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    final String phone = dataSnapshot.child("PHONE").getValue().toString();

                    phone_number = phone;
                    mobile.setText(phone);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    private void loadImage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String uid = mAuth.getCurrentUser().getUid();
                String email_add = profile.getEmail();

//                error_text.setText(uid);

                try {
                    if (!loading) {
                        Uri photoUrl = profile.getPhotoUrl();

                        Transformation transformation = new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidthDp(3)
                                .cornerRadiusDp(30)
                                .oval(true)
                                .build();

                        Picasso.with(EditProfile.this).load(photoUrl).resize(400, 400).centerCrop().transform(transformation).into(imageView);

                    }

                } catch (Exception e) {
                }
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(EditProfile.this, "Image selected, click on upload button", Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();

                    loading = true;
//                    updateData.setEnabled(true);
                    uploadImage = true;

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .borderWidthDp(3)
                            .cornerRadiusDp(30)
                            .oval(true)
                            .build();

                    Picasso.with(EditProfile.this).load(imageReturnedIntent.getData().toString()).resize(400, 400).centerCrop().transform(transformation).into(imageView);
                }
        }
    }


    public void OnUploadImage() {

        loading = false;

        if (uploadImage) {

            //create reference to images folder and assing a name to the file that will be uploaded
            imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
            //creating and showing progress dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);
            //starting upload
            uploadTask = imageRef.putFile(selectedImage);
            // Observe state change events such as progress, pause, and resume
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //sets and increments value of progressbar
                    progressDialog.incrementProgressBy((int) progress);
                }
            });
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(EditProfile.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(EditProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    //showing the uploaded image in ImageView using the download url

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .borderWidthDp(3)
                            .cornerRadiusDp(30)
                            .oval(true)
                            .build();

                    Picasso.with(EditProfile.this).load(downloadUrl).resize(400, 400).centerCrop().transform(transformation).into(imageView);

                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                    error_text.setText("Updated");
                                    }
                                }
                            });
                }
            });

        }
        uploadImage = false;

    }


    public void OnSelectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImage();
        getMobileEmail();
    }


    private void updateMobile(final String phone_nu)
    {

        final String user_id = mAuth.getCurrentUser().getUid();


        Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(user_id);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("PHONE").setValue(phone_nu);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    private void updateEmailMobile()
    {
        String phone = mobile.getText().toString();


        if (phone.isEmpty())
        {
            error_text.setText("Mobile number is empty");
            return;
        }

        if (phone_number!=phone)
        {
            if (phone.length()!=10)
            {
                error_text.setText("Invalid Mobile number");
                return;
            }
            //phone number has changed
            boolean b_phone = ValidationUtil.isValidPhoneNumber(phone);

            if (!b_phone)
            {
                error_text.setText("Invalid Mobile number");
                return;
            }

            //update phone
            updateMobile(phone);
        }

    }
    public void OnUpdateData(View view) {

        //upload Image
        OnUploadImage();
        updateEmailMobile();

        startActivity(new Intent(this, Home.class));

    }
}
