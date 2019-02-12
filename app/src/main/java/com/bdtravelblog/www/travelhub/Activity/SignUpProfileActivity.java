package com.bdtravelblog.www.travelhub.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.DataModel.User;
import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mikhaellopez.circularimageview.CircularImageView;
//import com.theartofdev.edmodo.cropper.CropImageView;

public class SignUpProfileActivity extends AppCompatActivity {

    EditText name,phone,country,city;
    Button finishBtn;
    CircularImageView proPic;


    //CropImageView cropImageView;

    CountryCodePicker phonePicker,countryPicker;
    EditText editTextCarrierNumber;

    FirebaseDatabase database;
    DatabaseReference userRoot;
    DatabaseReference userCount;

    FirebaseAuth auth;

    private StorageReference storage;
    Uri imageUri = null;
    Uri downloadUrl;
    private final int GALLARY_REQUEST_CODE = 1;

    int noOfUser = 0,uid = 0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_profile);

        name = (EditText)findViewById(R.id.signUpProfileFullName);
        city = (EditText)findViewById(R.id.signUpProfileCity);
        finishBtn = (Button) findViewById(R.id.signUpProfileFinishBtn);
        proPic = (CircularImageView) findViewById(R.id.profilePictureImageBtn);

        phonePicker = (CountryCodePicker) findViewById(R.id.signUpProfilePhone);
        countryPicker = (CountryCodePicker) findViewById(R.id.signUpProfileCountry);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);

        progressDialog = new ProgressDialog(this);


        auth = FirebaseAuth.getInstance();

        final String mail = auth.getCurrentUser().getEmail();

        if(auth.getCurrentUser()!=null) {

            downloadUrl = auth.getCurrentUser().getPhotoUrl();
            imageUri = auth.getCurrentUser().getPhotoUrl();
            name.setText(auth.getCurrentUser().getDisplayName());
            //phonePicker.setFullNumber(auth.getCurrentUser().getPhoneNumber());
        }


        if(auth.getCurrentUser().getPhotoUrl()!=null) {
            Toast.makeText(SignUpProfileActivity.this,auth.getCurrentUser().getPhotoUrl().toString(),Toast.LENGTH_SHORT).show();
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(proPic);
            //proPic.setImageURI(auth.getCurrentUser().getPhotoUrl());
            Log.e("user","ggl pic found");
        }


        //cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        //image btn gets the image
        proPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,GALLARY_REQUEST_CODE);
            }
        });


//        //Crop image complete listener
//        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
//            @Override
//            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
//                imageUri = result.getUri();
//                proPic.setImageURI(imageUri);
//            }
//        });


        phonePicker.registerCarrierNumberEditText(editTextCarrierNumber);

        storage = FirebaseStorage.getInstance().getReference();


        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        userRoot = database.getReference(FirebaseReferenceNames.USER_ROOT);

        userCount = database.getReference(FirebaseReferenceNames.USER_COUNT);


        userCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    noOfUser = dataSnapshot.getValue(Integer.class);
                    uid = 1 + noOfUser;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadUrl = auth.getCurrentUser().getPhotoUrl();

                progressDialog.setTitle("Creating your profile...");
                progressDialog.show();

                if(!phonePicker.isValidFullNumber()) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpProfileActivity.this,"Invalid format of Phone no.",Toast.LENGTH_SHORT).show();
                }else {
                    final User user = new User();
                    user.setUid("" + (uid));
                    user.setName(name.getText().toString());
                    user.setCountry(countryPicker.getSelectedCountryName());
                    String number = editTextCarrierNumber.getText().toString();
                    String formattedNum = phonePicker.getSelectedCountryCodeWithPlus()+ (number.length()>= 11? number.substring(1) : number);
                    user.setPhn(formattedNum);
                    user.setEmail(auth.getCurrentUser().getEmail());
                    userCount.setValue(uid);
                    if(imageUri!=null) {
                        postPic(user);
                    }else {
                        String[] s = auth.getCurrentUser().getEmail().split("@");
                        //user->email->user.class
                        userRoot.child(s[0]).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("user","no pic just saving");
                                saveUserInfo(name.getText().toString(),"");
                                progressDialog.dismiss();
                                startActivity(new Intent(SignUpProfileActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        }); //
                    }

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLARY_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();

            //cropImageView.setImageUriAsync(imageUri);

            proPic.setImageURI(imageUri);
        }
    }

    public void postPic(final User user) {

        String mail = auth.getCurrentUser().getEmail();
        if (mail==null)
            Toast.makeText(SignUpProfileActivity.this,"null mail",Toast.LENGTH_SHORT).show();

        else {
            Log.e("mail",mail);

            //return if gmail default pic selected no need to upload
            if(imageUri.equals(auth.getCurrentUser().getPhotoUrl())) {
                progressDialog.setTitle("Creating your profile...");

                Log.e("user","ggl pic, saving");

                //Toast.makeText(SignUpProfileActivity.this,downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                user.setProPic(""+auth.getCurrentUser().getPhotoUrl().toString());

                String[] s = auth.getCurrentUser().getEmail().split("@");
                //user->email->user.class
                userRoot.child(s[0]).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveUserInfo(name.getText().toString(),auth.getCurrentUser().getPhotoUrl().toString());
                        progressDialog.dismiss();
                        startActivity(new Intent(SignUpProfileActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpProfileActivity.this,"Failed to create your account",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            progressDialog.setTitle("Uploading your profile picture");
            StorageReference filePath = storage.child(mail).child("pro_pic");

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();

                    progressDialog.setTitle("Creating your profile...");

                    //Toast.makeText(SignUpProfileActivity.this,downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                    user.setProPic(""+downloadUrl);

                    String[] s = auth.getCurrentUser().getEmail().split("@");
                    //user->email->user.class
                    userRoot.child(s[0]).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("user","uploaded pic, saving");
                            saveUserInfo(name.getText().toString(),downloadUrl.toString());
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpProfileActivity.this, MainActivity.class));
                            finishAffinity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpProfileActivity.this,"Failed to create your account",Toast.LENGTH_SHORT).show();
                        }
                    }); //
                    //PostsDataProvider postsDataProvider = new PostsDataProvider(postDesc,downloadUrl.toString(),postTtl);
                    //newPost.setValue(postsDataProvider);

//                    newPost.child("title").setValue(postTtl);
//                    newPost.child("description").setValue(postDesc);
//                    newPost.child("image").setValue(downloadUrl.toString());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //FAILED to upload pic but creating account

                    Toast.makeText(SignUpProfileActivity.this,"Failed to upload your profile picture",Toast.LENGTH_SHORT).show();

                    progressDialog.setTitle("Creating your profile...");

                    //Toast.makeText(SignUpProfileActivity.this,downloadUrl.toString(),Toast.LENGTH_SHORT).show();
                    user.setProPic(""+downloadUrl);

                    String[] s = auth.getCurrentUser().getEmail().split("@");
                    //user->email->user.class
                    userRoot.child(s[0]).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("user","failed upload pic just saving");
                            saveUserInfo(name.getText().toString(),"");
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpProfileActivity.this, MainActivity.class));
                            finishAffinity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpProfileActivity.this,"Failed to create your account",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void saveUserInfo(String name,String userPhoto) {
        Toast.makeText(SignUpProfileActivity.this,"user "+name,Toast.LENGTH_SHORT).show();
        Toast.makeText(SignUpProfileActivity.this,"pic "+userPhoto,Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.name_key), name);
        editor.putString(getString(R.string.user_photo_key), userPhoto);
        editor.apply();
    }

}
