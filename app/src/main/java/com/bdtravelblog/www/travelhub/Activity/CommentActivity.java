package com.bdtravelblog.www.travelhub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdtravelblog.www.travelhub.DataModel.Comment;
import com.bdtravelblog.www.travelhub.DataModel.Post;
import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    public TextView postStory,postUsername,postTime,noOfLove,noOfComment;
    public CircularImageView postUserPic;
    //public LikeButton love;
    public CardView cardView;
    public Button comment,share;
    public RelativeLayout loveLayout;
    public LinearLayout countLayout;
    public LikeButton loveButton;

    public LinearLayout picLayout;
    public LinearLayout smallPicLayout;
    public RelativeLayout lastPicLayout;
    public ImageView pic1,pic2,pic3,pic4;
    public TextView morePicNUmber;

    LinearLayout commentLayout;
    EditText commentBox;
    ImageButton commentSend;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference feed = database.getReference(FirebaseReferenceNames.FEED_ROOT);
    DatabaseReference user = database.getReference(FirebaseReferenceNames.USER_ROOT);

    FirebaseAuth auth = FirebaseAuth.getInstance();
    final String[] s = auth.getCurrentUser().getEmail().split("@");

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        final Post post = (Post) getIntent().getSerializableExtra("post");

        postStory = (TextView) findViewById(R.id.commentStory);
        postUsername = (TextView) findViewById(R.id.commentUsername);
        postTime = (TextView) findViewById(R.id.commentPostTime);
        noOfLove = (TextView) findViewById(R.id.commentNumberOfLove);
        noOfComment = (TextView) findViewById(R.id.commentNumberOfComment);

        postUserPic = (CircularImageView) findViewById(R.id.commentUserPic);
        

        comment = (Button) findViewById(R.id.commentComment);
        share = (Button) findViewById(R.id.commentShare);
        loveLayout = (RelativeLayout) findViewById(R.id.commentLoveLayout);
        loveButton = (LikeButton) findViewById(R.id.commentLove);

        countLayout = (LinearLayout) findViewById(R.id.commentCountLayout);

        picLayout = (LinearLayout) findViewById(R.id.commentPictureLayout);
        smallPicLayout = (LinearLayout) findViewById(R.id.commentSmallPicLayout);
        lastPicLayout = (RelativeLayout) findViewById(R.id.commentLastPicLayout);
        pic1 = (ImageView) findViewById(R.id.commentPic1);
        pic2 = (ImageView) findViewById(R.id.commentPic2);
        pic3 = (ImageView) findViewById(R.id.commentPic3);
        pic4 = (ImageView) findViewById(R.id.commentPic4);
        morePicNUmber = (TextView) findViewById(R.id.commentMorePicNumber);

        commentLayout = (LinearLayout) findViewById(R.id.commentLinearLayout);
        commentBox = (EditText) findViewById(R.id.commentEditbox);
        commentSend = (ImageButton) findViewById(R.id.commentSendComment);

        if(!post.getProfilePicDownloadUrl().isEmpty()) {
            Glide.with(this).load(post.getProfilePicDownloadUrl()).into(postUserPic);
            postUserPic.setBorderColor(R.color.colorPrimaryDark);
        }

        postUsername.setText(post.getUsername());
        postTime.setText(post.getTime());
        if(post.getPostDetail().isEmpty())
            postStory.setVisibility(View.GONE);
        else
            postStory.setText(post.getPostDetail());

        noOfLove.setText(post.getLoveCount() + " Love");
        noOfComment.setText(post.getCommentCount() + " Comment");

        //Loading all comments
        final List<Comment> comments = post.getComments();
        if(comments==null) {

        }else {
            for(int i=0;i<comments.size();i++) {
                Comment comment = comments.get(i);

                addComment(comment);

            }
        }

        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Comment newComment = new Comment();
                newComment.setComment(commentBox.getText().toString());

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                String datetime = dateformat.format(c.getTime());
                newComment.setTime(datetime);

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                final String userPhotoLink = sharedPref.getString(getString(R.string.user_photo_key), "");
                final String usernameText = sharedPref.getString(getString(R.string.name_key), "username");

                newComment.setProfilePicDownloadUrl(userPhotoLink);

                newComment.setUsername(usernameText);

                List<Comment> com = post.getComments();
                if(com==null) {
                    com=new ArrayList<>();
                    com.add(newComment);
                }else{
                    com.add(newComment);
                }
                feed.child(post.getKey()).child(FirebaseReferenceNames.POST_COMMENTS).setValue(com);
                post.setComments(com);
                post.setCommentCount(Integer.parseInt(post.getCommentCount())+1+"");
                addComment(newComment);
                noOfComment.setText(post.getCommentCount()+" Comments");
                feed.child(post.getKey()).child(FirebaseReferenceNames.COMMENT_COUNT).setValue(post.getCommentCount());

                commentBox.setText("");
            }
        });
        Query query = user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).orderByChild(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).equalTo(post.getKey());
        Log.e("qry",query.toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    loveButton.setLiked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//                if(post.getLoveCount()!=null && post.getCommentCount()!=null) {
//                    if (post.getLoveCount().equals("0") && post.getCommentCount().equals("0"))
//                        countLayout.setVisibility(View.GONE);
//                    else {
//                        if (post.getLoveCount().equals("0"))
//                            noOfLove.setText("");
//                        else
//                            noOfLove.setText(post.getLoveCount() + " Love");
//
//                        if (post.getCommentCount().equals("0"))
//                            noOfComment.setText("");
//                        else
//                            noOfComment.setText(post.getCommentCount() + " Comment");
//                    }
//                }

        List<String> picList = post.getImageDownloadUrl();

        if(picList!=null && picList.size() !=0) {
            int size = picList.size();
            if(size==1) {
                Glide.with(this).load(picList.get(0)).into(pic1);
                smallPicLayout.setVisibility(View.GONE);
            }else if(size==2) {
                Glide.with(this).load(picList.get(0)).into(pic1);
                Glide.with(this).load(picList.get(1)).into(pic2);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,3);
                smallPicLayout.setLayoutParams(params);

                pic4.setVisibility(View.GONE);
                pic3.setVisibility(View.GONE);
            }else if(size==3) {
                Glide.with(this).load(picList.get(0)).into(pic1);
                Glide.with(this).load(picList.get(1)).into(pic2);
                Glide.with(this).load(picList.get(2)).into(pic3);

                pic4.setVisibility(View.GONE);
            }else {
                Glide.with(this).load(picList.get(0)).into(pic1);
                Glide.with(this).load(picList.get(1)).into(pic2);
                Glide.with(this).load(picList.get(2)).into(pic3);
                Glide.with(this).load(picList.get(3)).into(pic4);
                morePicNUmber.setText("+ "+(size-4));
            }
        }else {
            picLayout.setVisibility(View.GONE);
        }
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO COMPLETE COMMENT
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final int[] love = {Integer.parseInt(post.getLoveCount())};

        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loveButton.isLiked()) {

                    //noOfLove.setText(love[0] -1 + " Love");

                    feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null) {
                                Log.e("love",dataSnapshot.getValue().toString());
                                int loveCount = Integer.parseInt(dataSnapshot.getValue().toString());
                                feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).setValue(--love[0] +"");
                                noOfLove.setText(love[0] + " Love");

                                user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot==null) {
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).setValue("0");
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).child("0").setValue(post.getKey());
                                        }else {
                                            int totalLoved = Integer.parseInt(dataSnapshot.getValue().toString());
                                            //user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).child(FirebaseReferenceNames.USER_LOVED_POSTS_ID).child(""+totalLoved).removeValue();
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).orderByChild(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).equalTo(post.getKey())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            //if(dataSnapshot.exists()) {
                                                            for(DataSnapshot data: dataSnapshot.getChildren())
                                                                data.getRef().child(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).removeValue();
                                                            //}
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).setValue(--totalLoved+"");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    loveButton.setLiked(!loveButton.isLiked());
                }else{

                    //noOfLove.setText(love[0] +1 + " Love");


                    feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null) {
                                Log.e("love",dataSnapshot.getValue().toString());
                                int loveCount = Integer.parseInt(dataSnapshot.getValue().toString());
                                feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).setValue(++love[0] +"");
                                noOfLove.setText(love[0] + " Love");


                                user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue()==null) {
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).setValue("0");
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).child("0").setValue(post.getKey());
                                        }else {
                                            int totalLoved = Integer.parseInt(dataSnapshot.getValue().toString());
                                            user.child(s[0]).child(FirebaseReferenceNames.USER_TOTAL_LOVE).setValue(++totalLoved+"");
                                            DatabaseReference reference = user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).push();
                                            //reference.child(FirebaseReferenceNames.USER_LOVED_POSTS_ID).setValue(""+totalLoved);
                                            reference.child(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).setValue(post.getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    loveButton.setLiked(!loveButton.isLiked());
                }

            }
        });

    }

    public void addComment(Comment comment) {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);

        //Circular pro pic
        CircularImageView circleImageView = new CircularImageView(this);
        if(comment.getProfilePicDownloadUrl()==null || comment.getProfilePicDownloadUrl().isEmpty())
            Glide.with(this).load(R.drawable.ic_account_circle_grey600_24dp).into(circleImageView);
        else
            Glide.with(this).load(comment.getProfilePicDownloadUrl()).into(circleImageView);

        LinearLayout.LayoutParams picParams = new LinearLayout.LayoutParams(80,80);
        picParams.setMargins(0,0,15,0);
        circleImageView.setLayoutParams(picParams);

        //verticle lin layout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //uname
        TextView username = new TextView(this);
        username.setText(comment.getUsername());
        username.setTypeface(username.getTypeface(), Typeface.BOLD);
        LinearLayout.LayoutParams usernameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        usernameParams.setMargins(0,10,0,0);
        username.setLayoutParams(usernameParams);

        //comment
        TextView commentText = new TextView(this);
        commentText.setText(comment.getComment());
        commentText.setTextColor(Color.parseColor("#000000"));

        //time
        TextView time = new TextView(this);
        time.setText(comment.getTime());
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.setMargins(0,5,0,0);
        time.setLayoutParams(timeParams);

        linearLayout.addView(username);
        linearLayout.addView(commentText);
        linearLayout.addView(time);

        Log.e("cmnt",username.getText().toString());
        Log.e("cmnt",commentText.getText().toString());
        Log.e("cmnt",time.getText().toString());
        mainLayout.addView(circleImageView);
        mainLayout.addView(linearLayout);

        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainParams.setMargins(10,10,10,10);
        commentLayout.addView(mainLayout,mainParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
