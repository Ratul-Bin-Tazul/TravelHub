package com.bdtravelblog.www.travelhub.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdtravelblog.www.travelhub.Activity.CommentActivity;
import com.bdtravelblog.www.travelhub.Activity.ProfileActivity;
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

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Object> arrayList;
    private Context context;

    private final int POST = 0, IMAGE = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference feed = database.getReference(FirebaseReferenceNames.FEED_ROOT);
    DatabaseReference user = database.getReference(FirebaseReferenceNames.USER_ROOT);

    FirebaseAuth auth = FirebaseAuth.getInstance();
    final String[] s = auth.getCurrentUser().getEmail().split("@");

    public PostAdapter(ArrayList<Object> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.context = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout,parent,false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.post_layout, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new PostHolder(v);


        //PostAdapter.PostHolder PostHolder = new PostAdapter.PostHolder(view);
        switch (viewType) {
            case POST:
                View v1 = inflater.inflate(R.layout.post_layout, viewGroup, false);
                viewHolder = new PostHolder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.picture_layout, viewGroup, false);
                viewHolder = new PictureHolder(v2);
                break;
            default:

        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {



        //holder.message.setText(PostsDataProvider.getMessage());
        //holder.messageSent.setText(PostsDataProvider.getMessage());

        switch (viewHolder.getItemViewType()) {
            case POST:
                final PostHolder postViewholder = (PostHolder) viewHolder;
                final Post post = (Post) arrayList.get(position);
                if(!post.getProfilePicDownloadUrl().isEmpty()) {
                    Glide.with(context).load(post.getProfilePicDownloadUrl()).into(postViewholder.postUserPic);
                    postViewholder.postUserPic.setBorderColor(R.color.colorPrimaryDark);
                }

                postViewholder.postUserPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, ProfileActivity.class);
                        i.putExtra("id",post.getUserId());
                        context.startActivity(i);
                    }
                });
                postViewholder.postUsername.setText(post.getUsername());
                postViewholder.postTime.setText(post.getTime());
                if(post.getPostDetail().isEmpty())
                    postViewholder.postStory.setVisibility(View.GONE);
                else
                    postViewholder.postStory.setText(post.getPostDetail());

                postViewholder.noOfLove.setText(post.getLoveCount() + " Love");
                postViewholder.noOfComment.setText(post.getCommentCount() + " Comment");

                Query query = user.child(s[0]).child(FirebaseReferenceNames.USER_LOVED_POSTS).orderByChild(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).equalTo(post.getKey());
                Log.e("qry",query.toString());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            postViewholder.loveButton.setLiked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                if(post.getLoveCount()!=null && post.getCommentCount()!=null) {
//                    if (post.getLoveCount().equals("0") && post.getCommentCount().equals("0"))
//                        postViewholder.countLayout.setVisibility(View.GONE);
//                    else {
//                        if (post.getLoveCount().equals("0"))
//                            postViewholder.noOfLove.setText("");
//                        else
//                            postViewholder.noOfLove.setText(post.getLoveCount() + " Love");
//
//                        if (post.getCommentCount().equals("0"))
//                            postViewholder.noOfComment.setText("");
//                        else
//                            postViewholder.noOfComment.setText(post.getCommentCount() + " Comment");
//                    }
//                }

                List<String> picList = post.getImageDownloadUrl();

                if(picList!=null && picList.size() !=0) {
                    int size = picList.size();
                    if(size==1) {
                        Glide.with(context).load(picList.get(0)).into(postViewholder.pic1);
                        postViewholder.smallPicLayout.setVisibility(View.GONE);
                    }else if(size==2) {
                        Glide.with(context).load(picList.get(0)).into(postViewholder.pic1);
                        Glide.with(context).load(picList.get(1)).into(postViewholder.pic2);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,3);
                        postViewholder.smallPicLayout.setLayoutParams(params);

                        postViewholder.pic4.setVisibility(View.GONE);
                        postViewholder.pic3.setVisibility(View.GONE);
                    }else if(size==3) {
                        Glide.with(context).load(picList.get(0)).into(postViewholder.pic1);
                        Glide.with(context).load(picList.get(1)).into(postViewholder.pic2);
                        Glide.with(context).load(picList.get(2)).into(postViewholder.pic3);

                        postViewholder.pic4.setVisibility(View.GONE);
                    }else {
                        Glide.with(context).load(picList.get(0)).into(postViewholder.pic1);
                        Glide.with(context).load(picList.get(1)).into(postViewholder.pic2);
                        Glide.with(context).load(picList.get(2)).into(postViewholder.pic3);
                        Glide.with(context).load(picList.get(3)).into(postViewholder.pic4);
                        postViewholder.morePicNUmber.setText("+ "+(size-4));
                    }
                }else {
                    postViewholder.picLayout.setVisibility(View.GONE);
                }
                postViewholder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //TODO COMPLETE COMMENT

                        Intent i = new Intent(context,CommentActivity.class);
                        i.putExtra("post",post);
                        context.startActivity(i);
                    }
                });

                postViewholder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //TODO COMPLETE SHARE
                    }
                });


                //LOVE BUTTON
                final int[] love = {Integer.parseInt(post.getLoveCount())};

                postViewholder.loveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //ALREADY LIKED.UNLIKE
                        if(postViewholder.loveButton.isLiked()) {

                            //postViewholder.noOfLove.setText(love[0] -1 + " Love");

                            feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!=null) {
                                        Log.e("love",dataSnapshot.getValue().toString());
                                        int loveCount = Integer.parseInt(dataSnapshot.getValue().toString());
                                        feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).setValue(--love[0] +"");
                                        postViewholder.noOfLove.setText(love[0] + " Love");

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
                                                                    if(dataSnapshot.exists()) {
                                                                    for(DataSnapshot data: dataSnapshot.getChildren())
                                                                        data.getRef().child(FirebaseReferenceNames.USER_LOVED_POSTS_KEY).removeValue();
                                                                    }
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

                            postViewholder.loveButton.setLiked(!postViewholder.loveButton.isLiked());
                        }else{ //NOT LIKED.LOVE

                            //postViewholder.noOfLove.setText(love[0] +1 + " Love");


                            feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!=null) {
                                        Log.e("love",dataSnapshot.getValue().toString());
                                        int loveCount = Integer.parseInt(dataSnapshot.getValue().toString());
                                        feed.child(post.getKey()).child(FirebaseReferenceNames.LOVE_COUNT).setValue(++love[0] +"");
                                        postViewholder.noOfLove.setText(love[0] + " Love");


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

                            postViewholder.loveButton.setLiked(!postViewholder.loveButton.isLiked());
                        }

                    }
                });

                break;
            case IMAGE:
                PictureHolder vh2 = (PictureHolder) viewHolder;

                break;
            default:
                break;
        }

//        if(postsDataProvider.getPhotoUrl()==null) {
//            holder.postImg.setVisibility(View.GONE);
//        }else {
//            Glide.with(context)
//                    .load(postsDataProvider.getPhotoUrl())
//                    .into(holder.postImg);
//        }
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) instanceof Post) {
            return POST;
        } else if (arrayList.get(position) instanceof String) {
            return IMAGE;
        }
        return -1;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
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
        public PostHolder(final View itemView) {
            super(itemView);
            postStory = (TextView) itemView.findViewById(R.id.postItemStory);
            postUsername = (TextView) itemView.findViewById(R.id.postItemUsername);
            postTime = (TextView) itemView.findViewById(R.id.postItemPostTime);
            noOfLove = (TextView) itemView.findViewById(R.id.postItemNumberOfLove);
            noOfComment = (TextView) itemView.findViewById(R.id.postItemNumberOfComment);

            postUserPic = (CircularImageView) itemView.findViewById(R.id.postItemUserPic);

            cardView = (CardView) itemView.findViewById(R.id.postItemCard);

            comment = (Button) itemView.findViewById(R.id.postItemComment);
            share = (Button) itemView.findViewById(R.id.postItemShare);
            loveLayout = (RelativeLayout) itemView.findViewById(R.id.postItemLoveLayout);
            loveButton = (LikeButton) itemView.findViewById(R.id.postItemLove);

            countLayout = (LinearLayout) itemView.findViewById(R.id.postItemCountLayout);

            picLayout = (LinearLayout) itemView.findViewById(R.id.postItemPictureLayout);
            smallPicLayout = (LinearLayout) itemView.findViewById(R.id.postItemSmallPicLayout);
            lastPicLayout = (RelativeLayout) itemView.findViewById(R.id.postItemLastPicLayout);
            pic1 = (ImageView) itemView.findViewById(R.id.postItemPic1);
            pic2 = (ImageView) itemView.findViewById(R.id.postItemPic2);
            pic3 = (ImageView) itemView.findViewById(R.id.postItemPic3);
            pic4 = (ImageView) itemView.findViewById(R.id.postItemPic4);
            morePicNUmber = (TextView) itemView.findViewById(R.id.postItemMorePicNumber);
        }


    }

    public class PictureHolder extends RecyclerView.ViewHolder {
        public TextView postTitle,postOverview,likeCount;

        public PictureHolder(final View itemView) {
            super(itemView);

        }


    }

}
