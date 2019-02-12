package com.bdtravelblog.www.travelhub.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdtravelblog.www.travelhub.DataModel.User;
import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ImageView coverPic;
    ImageButton message,call,editTravelled,editInterests;
    CircularImageView profilePic;
    Button editProfile;
    TextView username,oneLineAbout;
    TextView postCount,photoCount,buddyCount;
    TextView about,email,phone,location,address;
    TextView noInterestsText,noTravelledText;
    FlexboxLayout interestsLayout,travelledLayout;

    FirebaseDatabase database;
    DatabaseReference feedRoot,userRoot,user;
    FirebaseAuth auth;

    User profileUser;

    List<String> interests = new ArrayList<>();
    List<String> travelled = new ArrayList<>();

    boolean ownPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //transparent status bar
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        coverPic = findViewById(R.id.profileCover);
        message = findViewById(R.id.profileMessage);
        call = findViewById(R.id.profileCall);
        profilePic = findViewById(R.id.profileMainPicture);
        editProfile = findViewById(R.id.profileEditProfile);
        username = findViewById(R.id.profileUsername);
        oneLineAbout = findViewById(R.id.profileOneLineAbout);
        postCount = findViewById(R.id.profilePostsCount);
        photoCount = findViewById(R.id.profilePhotosCount);
        buddyCount = findViewById(R.id.profileBuddiesCount);
        about = findViewById(R.id.profileAbout);
        email = findViewById(R.id.profileEmail);
        phone = findViewById(R.id.profilePhone);
        location = findViewById(R.id.profileLocation);
        address = findViewById(R.id.profileAddress);
        editTravelled = findViewById(R.id.profileTravelledEdit);
        editInterests = findViewById(R.id.profileInterestsEdit);

        noInterestsText = findViewById(R.id.profileNoInterestsText);
        noTravelledText = findViewById(R.id.profileNoTravelledText);
        interestsLayout = findViewById(R.id.profileInterestsLayout);
        travelledLayout = findViewById(R.id.profileTravelledLayout);

        interestsLayout.setFlexDirection(FlexDirection.ROW);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final String userEmail = auth.getCurrentUser().getEmail();
        final String[] s = userEmail.split("@");
        userRoot = database.getReference().child(FirebaseReferenceNames.USER_ROOT);



        //String postUserId = getIntent().getStringExtra("postUserId");


        String id = getIntent().getStringExtra("id");


        if(id!=null) {

            //NOT owner profile. Hide all edit
            if(!id.equals(s[0])) {
                editProfile.setVisibility(View.GONE);
                editTravelled.setVisibility(View.GONE);
                editInterests.setVisibility(View.GONE);
            }


            user = userRoot.child(id);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        profileUser = dataSnapshot.getValue(User.class);


                        if(profileUser.getProPic()!=null)
                            Glide.with(ProfileActivity.this).load(profileUser.getProPic()).into(profilePic);

                        if(profileUser.getCoverPic()!=null)
                            Glide.with(ProfileActivity.this).load(profileUser.getCoverPic()).into(coverPic);

                        if(profileUser.getName()!=null)
                            username.setText(profileUser.getName());
                        if(profileUser.getOneLineAbout()!=null)
                            oneLineAbout.setText(profileUser.getOneLineAbout());
                        else
                            oneLineAbout.setVisibility(View.GONE);
                        //if(profileUser.getTotalPost()!=null)
                            postCount.setText(""+profileUser.getTotalPost());
                        //if(profileUser.getTotalPhoto()!=null)
                            photoCount.setText(""+profileUser.getTotalPhoto());
                        //if(profileUser.getTotalBuddy()!=null)
                            buddyCount.setText(""+profileUser.getTotalBuddy());
                        if(profileUser.getAbout()!=null)
                            about.setText(profileUser.getAbout());
                        if(profileUser.getEmail()!=null)
                            email.setText(profileUser.getEmail());
                        if(profileUser.getPhn()!=null)
                            phone.setText(profileUser.getPhn());
                        if(profileUser.getCountry()!=null || !profileUser.getCountry().isEmpty())
                            location.setText(profileUser.getCountry());
                        else
                            location.setText("Not set by user yet");
                        if(profileUser.getAddress()!=null || !profileUser.getAddress().isEmpty())
                            address.setText(profileUser.getAddress());
                        else
                            address.setText("Not set by user yet");

                        travelled = profileUser.getTravelledList();
                        if(travelled!=null) {
                            if (travelled.size() > 0)
                                noTravelledText.setVisibility(View.GONE);

                            for (int i = 0; i < travelled.size(); i++) {
                                TextView textView = new TextView(ProfileActivity.this);
                                textView.setText(travelled.get(i));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }
                                textView.setBackground(getResources().getDrawable(R.drawable.item_interest));
                                textView.setTextColor(Color.parseColor("#388E3C"));
                                textView.setPadding(20,15,20,15);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(10,10,10,10);

                                travelledLayout.addView(textView,layoutParams);
                            }
                        }

                        interests = profileUser.getInterestList();
                        if(interests!=null) {
                            if (interests.size() > 0)
                                noInterestsText.setVisibility(View.GONE);

                            for (int i = 0; i < interests.size(); i++) {
                                TextView textView = new TextView(ProfileActivity.this);
                                textView.setText(interests.get(i));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }
                                textView.setBackground(getResources().getDrawable(R.drawable.item_interest));
                                textView.setTextColor(Color.parseColor("#388E3C"));
                                textView.setPadding(20,15,20,15);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(10,10,10,10);

                                interestsLayout.addView(textView,layoutParams);
                            }
                        }

                        //TODO: DESIGN AND FINISH TRAVELLED
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                    i.putExtra("username",profileUser.getName());

                    if(profileUser.getCoverPic()!=null)
                        i.putExtra("cover",profileUser.getCoverPic());
                    else
                        i.putExtra("cover","");

                    if(profileUser.getProPic()!=null)
                        i.putExtra("proPic",profileUser.getProPic());
                    else
                        i.putExtra("proPic","");

                    if(profileUser.getOneLineAbout()!=null)
                        i.putExtra("oneLine",profileUser.getOneLineAbout());
                    else
                        i.putExtra("oneLine","");

                    if(profileUser.getAbout()!=null)
                        i.putExtra("about",profileUser.getAbout());
                    else
                        i.putExtra("about","");

                    if(profileUser.getEmail()!=null)
                        i.putExtra("email",profileUser.getEmail());
                    else
                        i.putExtra("email","");

                    if(profileUser.getCountry()!=null)
                        i.putExtra("location",profileUser.getCountry());
                    else
                        i.putExtra("location","");

                    if(profileUser.getPhn()!=null)
                        i.putExtra("phone",profileUser.getPhn());
                    else
                        i.putExtra("phone","");

                    if(profileUser.getAddress()!=null)
                        i.putExtra("address",profileUser.getAddress());
                    else
                        i.putExtra("address","");

                    startActivity(i);
                }
            });
        }

        editInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,EditInterestsActivity.class);
                i.putExtra("interestList", (Serializable) interests);
                startActivity(i);
            }
        });

        editTravelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,EditTravelledActivity.class);
                i.putExtra("travelledList", (Serializable) travelled);
                startActivity(i);
            }
        });
    }
}
