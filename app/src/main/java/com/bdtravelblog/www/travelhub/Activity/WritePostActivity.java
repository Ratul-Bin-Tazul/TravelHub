package com.bdtravelblog.www.travelhub.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.DataModel.Post;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WritePostActivity extends AppCompatActivity {

    CircularImageView userPhoto;
    ImageButton pickImage;
    ImageButton postSetting;
    TextView username;
    EditText story;
    private int PICK_IMG = 10;

    Button postButton;

    LinearLayout imgLayout;


    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference feedRoot,userRoot;
    FirebaseAuth auth;

    ArrayList<Uri> imageUriList = new ArrayList<>();
    ArrayList<Uri> imageLinkList = new ArrayList<>();

    ProgressDialog progressDialog;

    final int[] totalPost = {0};
    final int[] totalPhoto = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        userPhoto = (CircularImageView)findViewById(R.id.writePostProPic);
        pickImage = (ImageButton) findViewById(R.id.writePostImagePickerButton);
        username = (TextView) findViewById(R.id.writePostUsername);
        story = (EditText) findViewById(R.id.writePostStory);
        imgLayout = (LinearLayout) findViewById(R.id.writePostImageLayout);

        postButton = (Button) findViewById(R.id.writePostPostButton);

        progressDialog = new ProgressDialog(this);;

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        feedRoot = database.getReference(FirebaseReferenceNames.FEED_ROOT);
        userRoot = database.getReference(FirebaseReferenceNames.USER_ROOT);
        storageReference = storage.getReference(FirebaseReferenceNames.POST_PIC);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String userPhotoLink = sharedPref.getString(getString(R.string.user_photo_key), "");
        final String usernameText = sharedPref.getString(getString(R.string.name_key), "username");

        username.setText(usernameText);
        if(!userPhotoLink.isEmpty()) {
            Glide.with(this).load(userPhotoLink).into(userPhoto);
        }else {
            Glide.with(this).load(R.drawable.ic_account_circle_grey600_24dp).into(userPhoto);
        }


        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), PICK_IMG);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageUriList.size()==0) {
                    progressDialog.setTitle("Posting your story... ");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    //pushing unique post
                    DatabaseReference postRef = feedRoot.push();

                    final String[] s = auth.getCurrentUser().getEmail().split("@");


                    userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_POST).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                totalPost[0] = dataSnapshot.getValue(Integer.class);
                            }else{
                                userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_POST).setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_PHOTO).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                totalPhoto[0] = dataSnapshot.getValue(Integer.class);
                            }else{
                                userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_PHOTO).setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //adding the unique key to list of user post
                    String path = postRef.getKey();
                    userRoot.child(s[0]).child(FirebaseReferenceNames.USER_POSTS).child(totalPost[0]+1+"").setValue(path);


                    Post post = new Post();
                    post.setKey(path);

                    post.setUsername(usernameText);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                    String datetime = dateformat.format(c.getTime());
                    post.setTime(datetime);

                    post.setProfilePicDownloadUrl(userPhotoLink);
                    post.setLoveCount("0");
                    post.setCommentCount("0");
                    post.setPostDetail(story.getText().toString());
                    post.setUserId(s[0]);
                    post.setType("story");
                    post.setViewCount("0");
                    Log.e("userId",s[0]);

                    postRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_POST).setValue(totalPost[0]+1);
                            userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_PHOTO).setValue(totalPhoto[0]+imageLinkList.size());
                            progressDialog.dismiss();
                            Toast.makeText(WritePostActivity.this,"Posted successfully",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }else {
                    //First uploading all the image
                    uploadImage(usernameText,userPhotoLink,1);

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imageUriList.add(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                addImgPopup(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    //add selected small images at the bottom of the screen
    private void addImgPopup(Bitmap bitmap) {
        //ImageView Setup
        ImageView imageView = new ImageView(this);

        //setting image resource
        imageView.setImageBitmap(bitmap);

        int height = convertDpToPx(100);
        int marginSide = convertDpToPx(10);
        int marginUp = convertDpToPx(3);
        //setting image position
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(height/2,height);
        layoutParams.setMargins(marginSide,marginUp,marginSide,0);
        imageView.setLayoutParams(layoutParams);

        //adding view to layout
        imgLayout.addView(imageView);
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }

    private int convertPxToDp(int px){
        return Math.round(px/(Resources.getSystem().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }

    //Upload a image related with the post
    private void uploadImage(final String username, final String userPhoto, final int number) {

        progressDialog.setTitle("Uploading image... ");
        progressDialog.setCancelable(false);
        progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUriList.get(0))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            imageUriList.remove(0);
                            imageLinkList.add(taskSnapshot.getDownloadUrl());
                            if(!imageUriList.isEmpty()) {
                                uploadImage(username,userPhoto,number+1);
                            }else {
                                post(username,userPhoto,imageLinkList);
                            }
                            //Toast.makeText(WritePostActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(WritePostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Image "+number+" Uploaded "+(int)progress+"%");
                        }
                    });

    }

    public void post(String usernameText, String userPhotoLink, final ArrayList<Uri> imageLinkList) {

        progressDialog.dismiss();

        //pushing unique post
        DatabaseReference postRef = feedRoot.push();

        final String[] s = auth.getCurrentUser().getEmail().split("@");

        //adding the unique key to list of user post
        String path = postRef.getKey();
        userRoot.child(s[0]).child(FirebaseReferenceNames.USER_POSTS).push().setValue(path);

//        userRoot.child(s[0]).child(FirebaseReferenceNames.USER_POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot!=null) {
//                    Map<String, String> td = (HashMap<String,String>) dataSnapshot.getValue();
//                    ArrayList<String> list = new ArrayList<>(td.values());
//                    list.add()
//
//                    //uid = 1 + noOfUser;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//

        Post post = new Post();
        post.setKey(path);
        post.setUsername(usernameText);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());
        post.setTime(datetime);

        post.setProfilePicDownloadUrl(userPhotoLink);
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i=0;i<imageLinkList.size();i++) {
            arrayList.add(imageLinkList.get(i).toString());
        }
        post.setImageDownloadUrl(arrayList);
        post.setLoveCount("0");
        post.setCommentCount("0");
        post.setPostDetail(story.getText().toString());
        post.setUserId(s[0]);
        post.setType("story");
        post.setViewCount("0");
        Log.e("userId",s[0]);

        postRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userRoot.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_PHOTO).setValue(totalPhoto[0]+imageLinkList.size());

                Toast.makeText(WritePostActivity.this,"Posted successfully",Toast.LENGTH_SHORT).show();

                startActivity(new Intent(WritePostActivity.this,MainActivity.class));
                finishAffinity();
            }
        });
    }
}
