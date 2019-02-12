package com.bdtravelblog.www.travelhub.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mvc.imagepicker.ImagePicker;

import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {

    Button coverChangeButton,profilePicChangeButton,saveButton;
    ImageView cover;
    CircularImageView profileView;
    EditText username,oneLineAbout,about,email,phone,location,address;

    Uri newCover,newProfilePic;
    private int PROFILE_REQUEST_CODE = 1;
    private int COVER_REQUEST_CODE = 2;

    ProgressDialog progressDialog;

    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference userRoot;
    FirebaseAuth auth;

    String user = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        cover = findViewById(R.id.profileEditCover);
        profileView = findViewById(R.id.profileEditProfilePicture);

        coverChangeButton = findViewById(R.id.profileEditChangeCover);
        profilePicChangeButton = findViewById(R.id.profileEditChangeProfilePicture);
        saveButton = findViewById(R.id.profileEditSave);

        username = findViewById(R.id.profileEditUsername);
        oneLineAbout = findViewById(R.id.profileEditOneLineAbout);
        about = findViewById(R.id.profileEditAbout);
        email = findViewById(R.id.profileEditEmail);
        phone = findViewById(R.id.profileEditPhone);
        location = findViewById(R.id.profileEditLocation);
        address = findViewById(R.id.profileEditAddress);


        progressDialog = new ProgressDialog(this);

        String coverUrl = getIntent().getStringExtra("cover");
        String proPicUrl = getIntent().getStringExtra("proPic");
        String usernameText = getIntent().getStringExtra("username");
        String oneLineText = getIntent().getStringExtra("oneLine");
        String aboutText = getIntent().getStringExtra("about");
        String emailText = getIntent().getStringExtra("email");
        final String phoneText = getIntent().getStringExtra("phone");
        String locationText = getIntent().getStringExtra("location");
        String addressText = getIntent().getStringExtra("address");

        if(!coverUrl.isEmpty())
            Glide.with(this).load(coverUrl).into(cover);
        if(!proPicUrl.isEmpty())
            Glide.with(this).load(proPicUrl).into(profileView);

        username.setText(usernameText);
        oneLineAbout.setText(oneLineText);
        about.setText(aboutText);
        email.setText(emailText);
        location.setText(locationText);
        phone.setText(phoneText);
        address.setText(addressText);

        storage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final String userEmail = auth.getCurrentUser().getEmail();
        final String[] s = userEmail.split("@");
        user = s[0];

        userRoot = database.getReference().child(FirebaseReferenceNames.USER_ROOT);

        coverChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,COVER_REQUEST_CODE);

            }
        });

        profilePicChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,PROFILE_REQUEST_CODE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Saving your profile");
                progressDialog.setCancelable(false);
                progressDialog.show();

                uploadProPic();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            newProfilePic = data.getData();

            //cropImageView.setImageUriAsync(imageUri);

            profileView.setImageURI(newProfilePic);
        }
        else if(requestCode==COVER_REQUEST_CODE && resultCode == RESULT_OK) {
            newCover = data.getData();

            //cropImageView.setImageUriAsync(imageUri);

            cover.setImageURI(newCover);
        }
    }

    public void uploadProPic() {

        if(newProfilePic!=null) {

            final String[] prevUrl = {""};

            //get current pro pic url
            userRoot.child(user).child(FirebaseReferenceNames.USER_PRO_PIC).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                        prevUrl[0] = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (!prevUrl[0].isEmpty()) {

                StorageReference prevPicLink = storage.getReferenceFromUrl(prevUrl[0]);
                prevPicLink.delete();

            }

            StorageReference ref = storage.getReference().child(user).child("pro_pic");
            ref.putFile(newProfilePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    userRoot.child(user).child(FirebaseReferenceNames.USER_PROPIC).setValue(taskSnapshot.getDownloadUrl().toString());
                    uploadCoverPic();
                }
            });

        }else {
            uploadCoverPic();
        }

    }

    public void uploadCoverPic() {

        if(newCover!=null) {
            final String[] prevCover = {""};
            userRoot.child(user).child(FirebaseReferenceNames.USER_COVERPIC).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists())
                        prevCover[0] = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


                if (!prevCover[0].isEmpty()) {

                    StorageReference prevCoverLink = storage.getReferenceFromUrl(prevCover[0]);
                    prevCoverLink.delete();

                }

                StorageReference coverRef = storage.getReference().child(user).child("cover_pic");
                coverRef.putFile(newCover).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        userRoot.child(user).child(FirebaseReferenceNames.USER_COVERPIC).setValue(taskSnapshot.getDownloadUrl().toString());
                        updateData();
                    }
                });

        }else {
            updateData();
        }
    }

    public void updateData() {

        userRoot.child(user).child(FirebaseReferenceNames.USER_NAME).setValue(username.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_PHONE).setValue(phone.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_EMAIL).setValue(email.getText().toString());
        auth.getCurrentUser().updateEmail(username.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_COUNTRY).setValue(location.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_ADDRESS).setValue(address.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_ONE_LINE_ABOUT).setValue(oneLineAbout.getText().toString());
        userRoot.child(user).child(FirebaseReferenceNames.USER_ABOUT).setValue(about.getText().toString());

        progressDialog.dismiss();
        finish();

    }
}
