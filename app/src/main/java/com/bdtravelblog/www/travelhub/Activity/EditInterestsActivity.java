package com.bdtravelblog.www.travelhub.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;
import java.util.List;

public class EditInterestsActivity extends AppCompatActivity {

    AutoLabelUI autoLabelUI;
    EditText customLabel;
    Button addCustomLabel;
    TextView noLabelText;

    TextView itemAdventure,itemArt,itemBackpacking,itemBeaches,itemMountains,itemHistorical,itemNature,itemRomantic,
            itemBiking,itemPubCrawling,itemShopping,itemCulture,itemHomeStay,itemTrekking,itemWaterfalls,itemDiving,
            itemCaving,itemOffRoading;

    List<String> interestList = new ArrayList<>();
    List<String> commonInterestList = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference feedRoot,userRoot,user;
    FirebaseAuth auth;

    String userId;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interests);

        autoLabelUI =  findViewById(R.id.label_view);
        customLabel =  findViewById(R.id.editInterestsAddCustomEditText);
        addCustomLabel =  findViewById(R.id.editInterestsAddCustomButton);
        noLabelText =  findViewById(R.id.editInterestsNoLabelText);

        itemAdventure =  findViewById(R.id.editInterestsAdventure);
        itemArt =  findViewById(R.id.editInterestsArt);
        itemBackpacking =  findViewById(R.id.editInterestsBackpacking);
        itemBeaches =  findViewById(R.id.editInterestsBeaches);
        itemBiking =  findViewById(R.id.editInterestsBiking);
        itemCaving =  findViewById(R.id.editInterestsCaving);
        itemCulture =  findViewById(R.id.editInterestsCulture);
        itemDiving =  findViewById(R.id.editInterestsDiving);
        itemHistorical =  findViewById(R.id.editInterestsHistorical);
        itemHomeStay =  findViewById(R.id.editInterestsHomeStay);
        itemMountains =  findViewById(R.id.editInterestsMountains);
        itemNature =  findViewById(R.id.editInterestsNature);
        itemOffRoading =  findViewById(R.id.editInterestsOffRoading);
        itemPubCrawling =  findViewById(R.id.editInterestsPub);
        itemRomantic =  findViewById(R.id.editInterestsRomantic);
        itemShopping =  findViewById(R.id.editInterestsShopping);
        itemTrekking =  findViewById(R.id.editInterestsTrekking);
        itemWaterfalls =  findViewById(R.id.editInterestsWaterfall);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String[] userEmail = auth.getCurrentUser().getEmail().split("@");
        userId = userEmail[0];
        userRoot = database.getReference().child(FirebaseReferenceNames.USER_ROOT);


        if(getIntent().getSerializableExtra("interestList")!=null) {
            interestList = (ArrayList<String>) getIntent().getSerializableExtra("interestList");
        }
        
        noLabelText.setVisibility(View.GONE);

        //Adding the previous list of interests
        for (int i=0;i<interestList.size();i++) {
            String interest = interestList.get(i);
            autoLabelUI.addLabel(interest);

            TextView textView = null;
            if(interest.equals("Adventure")) {
                textView = itemAdventure;
            }
            else if(interest.equals("Art & Crafts"))
                textView = itemArt;
            else if(interest.equals("Backpacking"))
                textView = itemBackpacking;
            else if(interest.equals("Beaches"))
                textView = itemBeaches;
            else if(interest.equals("Mountains"))
                textView = itemMountains;
            else if(interest.equals("Historical"))
                textView = itemHistorical;
            else if(interest.equals("Nature"))
                textView = itemNature;
            else if(interest.equals("Romantic"))
                textView = itemRomantic;
            else if(interest.equals("Biking"))
                textView = itemBiking;
            else if(interest.equals("Pub Crawling"))
                textView = itemPubCrawling;
            else if(interest.equals("Shopping"))
                textView = itemShopping;
            else if(interest.equals("Culture"))
                textView = itemCulture;
            else if(interest.equals("Home Stay"))
                textView = itemHomeStay;
            else if(interest.equals("Water Falls"))
                textView = itemWaterfalls;
            else if(interest.equals("Diving"))
                textView = itemDiving;
            else if(interest.equals("Caving"))
                textView = itemCaving;
            else if(interest.equals("Off Roading"))
                textView = itemOffRoading;

            if(textView!=null) {
                textView.setBackground(getResources().getDrawable(R.drawable.item_text_view_selected));
                textView.setTextColor(Color.parseColor("#ffffff"));
            }
                //last 
        }


        commonInterestList.add(itemAdventure.getText().toString());
        commonInterestList.add(itemArt.getText().toString());
        commonInterestList.add(itemBackpacking.getText().toString());
        commonInterestList.add(itemBeaches.getText().toString());
        commonInterestList.add(itemMountains.getText().toString());
        commonInterestList.add(itemHistorical.getText().toString());
        commonInterestList.add(itemNature.getText().toString());
        commonInterestList.add(itemRomantic.getText().toString());
        commonInterestList.add(itemBiking.getText().toString());
        commonInterestList.add(itemPubCrawling.getText().toString());
        commonInterestList.add(itemShopping.getText().toString());
        commonInterestList.add(itemCulture.getText().toString());

        commonInterestList.add(itemHomeStay.getText().toString());
        commonInterestList.add(itemWaterfalls.getText().toString());
        commonInterestList.add(itemDiving.getText().toString());
        commonInterestList.add(itemCaving.getText().toString());
        commonInterestList.add(itemOffRoading.getText().toString());


//        for (int i=0;i<commonInterestList.size();i++) {
//            if(interestList.contains(commonInterestList.get(i))) {
//
//            }
//        }

        addCustomLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(customLabel.getText().toString().isEmpty()) {
                    Toast.makeText(EditInterestsActivity.this, "Please type your interest to add", Toast.LENGTH_SHORT).show();
                }else {
                    String newLabel = customLabel.getText().toString();
                    autoLabelUI.addLabel(newLabel,interestList.size());
                    interestList.add(newLabel);
                    customLabel.getText().clear();
                }
            }
        });

        //To know when you have deleted all Labels, you will need to implement the onLabelsEmpty interface:
        autoLabelUI.setOnLabelsEmptyListener(new AutoLabelUI.OnLabelsEmptyListener() {
            @Override
            public void onLabelsEmpty() {
                noLabelText.setVisibility(View.VISIBLE);
            }
        });

        //To know when you have deleted a Label, you will need to implement the onRemoveLabel interface:
        autoLabelUI.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                Log.e("pos",position+"");

                if(position==-1) {
                    Label label = (Label)view;

                    String interest = label.getText().trim();

                    Log.e("item",label.getText());

                    TextView textView;
                    if(interest.equals("Adventure")) {
                        textView = itemAdventure;
                    }
                    else if(interest.equals("Art & Crafts"))
                        textView = itemArt;
                    else if(interest.equals("Backpacking"))
                        textView = itemBackpacking;
                    else if(interest.equals("Beaches"))
                        textView = itemBeaches;
                    else if(interest.equals("Mountains"))
                        textView = itemMountains;
                    else if(interest.equals("Historical"))
                        textView = itemHistorical;
                    else if(interest.equals("Nature"))
                        textView = itemNature;
                    else if(interest.equals("Romantic"))
                        textView = itemRomantic;
                    else if(interest.equals("Biking"))
                        textView = itemBiking;
                    else if(interest.equals("Pub Crawling"))
                        textView = itemPubCrawling;
                    else if(interest.equals("Shopping"))
                        textView = itemShopping;
                    else if(interest.equals("Culture"))
                        textView = itemCulture;
                    else if(interest.equals("Home Stay"))
                        textView = itemHomeStay;
                    else if(interest.equals("Water Falls"))
                        textView = itemWaterfalls;
                    else if(interest.equals("Diving"))
                        textView = itemDiving;
                    else if(interest.equals("Caving"))
                        textView = itemCaving;
                    else
                        textView = itemOffRoading;


                    //last else if(interest.equals("Off Roading"))

                    textView.setBackground(getResources().getDrawable(R.drawable.item_text_view));
                    textView.setTextColor(Color.parseColor("#000000"));
                    interestList.remove(textView.getText().toString());
                    autoLabelUI.removeLabel(textView.getText().toString());

                    interestList.remove(interest);
                }else {
                    Log.e("item","else");
                    interestList.remove(position);
                }
            }
        });


    }

    public void itemSelected(View view) {
        TextView textView = (TextView) view;
        if(interestList.contains(textView.getText().toString())) {
            textView.setBackground(getResources().getDrawable(R.drawable.item_text_view));
            textView.setTextColor(Color.parseColor("#000000"));
            interestList.remove(textView.getText().toString());
            autoLabelUI.removeLabel(textView.getText().toString());

            Log.e("remove",interestList.size()+" "+ textView.getText().toString());

        }else {
            textView.setBackground(getResources().getDrawable(R.drawable.item_text_view_selected));
            textView.setTextColor(Color.parseColor("#ffffff"));
            //autoLabelUI.addLabel(textView.getText().toString(),interestList.size());
            autoLabelUI.addLabel(textView.getText().toString());
            interestList.add(textView.getText().toString());

            Log.e("inserted",interestList.size()+""+ interestList.get(interestList.size()-1));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_header_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Saving interests...");
            //progressDialog.show();

            DatabaseReference user = userRoot.child(userId).child(FirebaseReferenceNames.USER_INTERESTS_LIST);
            user.setValue(interestList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(EditInterestsActivity.this,"Saved interests",Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(EditInterestsActivity.this,ProfileActivity.class);
                    i.putExtra("id",userId);
                    startActivity(i);
                    
                    finishAffinity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditInterestsActivity.this,"Failed to Save interests",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditInterestsActivity.this,ProfileActivity.class));
                    finishAffinity();
                }
            });
            return true;
        }
        return false;
    }
}
