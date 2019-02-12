package com.bdtravelblog.www.travelhub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bdtravelblog.www.travelhub.Adapter.PostAdapter;
import com.bdtravelblog.www.travelhub.DataModel.Post;
import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int messageBadgeCount=1,notificationBadgeCount=1;

    com.github.clans.fab.FloatingActionButton fabPost,fabPic;
    //RecyclerView postRecyclerview;

    public RecyclerView postRecycleView;
    public RecyclerView.Adapter postAdapter;
    public RecyclerView.LayoutManager postLayoutManager;

    public ArrayList<Object> postArrayList = new ArrayList<>();
    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Animations
        //Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        //Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.startAnimation(show_fab_1);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        CircularImageView drawerProPic = (CircularImageView)headerView.findViewById(R.id.drawerProPic);
        TextView drawername = (TextView) headerView.findViewById(R.id.drawerUsername);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String userPhoto = sharedPref.getString(getString(R.string.user_photo_key), "");
        String name = sharedPref.getString(getString(R.string.name_key), "name");

        Log.e("uname",name);
        Log.e("upic",userPhoto);
        drawername.setText(name);

        if(!userPhoto.isEmpty()) {
            Glide.with(this).load(userPhoto).into(drawerProPic);
        }else {
            Glide.with(this).load(R.drawable.ic_account_circle_grey600_24dp).into(drawerProPic);
        }


        postRecycleView = (RecyclerView)findViewById(R.id.postRecyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        postRecycleView.setHasFixedSize(true);


        // use a linear layout manager
        postLayoutManager = new LinearLayoutManager(this);
        postRecycleView.setLayoutManager(postLayoutManager);

        postAdapter = new PostAdapter(postArrayList,this);
        postRecycleView.setAdapter(postAdapter);

        postAdapter.notifyDataSetChanged();
        postRecycleView.swapAdapter(postAdapter,false);

        DatabaseReference feedRef = FirebaseDatabase.getInstance().getReference().child(FirebaseReferenceNames.FEED_ROOT);

        feedRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);

                postArrayList.add(post);
                postAdapter.notifyDataSetChanged();
//                postRecycleView.swapAdapter(postAdapter,false);
                Log.e("error","added value ");


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fabPic  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabPicture);
        fabPost  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabPost);


        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,WritePostActivity.class));
            }
        });

        fabPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,UploadPictureActivity.class));

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_message) {
            messageBadgeCount = 0;
            ActionItemBadge.update(item, messageBadgeCount);
            //you can add some logic (hide it if the count == 0)
               // ActionItemBadge.hide(item);

            return true;
        }
        else if(id == R.id.action_notification) {
            notificationBadgeCount=0;
            ActionItemBadge.update(item, notificationBadgeCount);
            //ActionItemBadge.hide(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused)
            recreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }
}
