package com.bdtravelblog.www.travelhub.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.dpizarro.autolabel.library.AutoLabelUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EditTravelledActivity extends AppCompatActivity {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    CardView addTravelledCard;
    AutoLabelUI autoLabelUI;

    List<String> travelledList = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference feedRoot,userRoot,user;
    FirebaseAuth auth;

    String userId;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_travelled);

        addTravelledCard = findViewById(R.id.editTravelledPlaceSelectCard);
        autoLabelUI = findViewById(R.id.editTravelled_label_view);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String[] userEmail = auth.getCurrentUser().getEmail().split("@");
        userId = userEmail[0];
        userRoot = database.getReference().child(FirebaseReferenceNames.USER_ROOT);

        if(getIntent().getSerializableExtra("travelledList")!=null) {
            travelledList = (ArrayList<String>) getIntent().getSerializableExtra("travelledList");
        }


        //Adding the previous list of travelled
        for (int i=0;i<travelledList.size();i++) {
            String interest = travelledList.get(i);
            autoLabelUI.addLabel(interest,i);
        }

            autoLabelUI.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                travelledList.remove(position);
            }
        });


        addTravelledCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(EditTravelledActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                autoLabelUI.addLabel(place.getName().toString(),travelledList.size());
                travelledList.add(place.getName().toString());
                Log.e("place", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("place", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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
            progressDialog.setMessage("Saving travelled places...");
            //progressDialog.show();

            DatabaseReference user = userRoot.child(userId).child(FirebaseReferenceNames.USER_TRAVELLED_LIST);

            user.setValue(travelledList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTravelledActivity.this,"Saved travelled places",Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(EditTravelledActivity.this,ProfileActivity.class);
                    i.putExtra("id",userId);
                    startActivity(i);

                    finishAffinity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTravelledActivity.this,"Failed to Save travelled places",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditTravelledActivity.this,ProfileActivity.class));
                    finishAffinity();
                }
            });
            return true;
        }
        return false;
    }
}
