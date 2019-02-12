package com.bdtravelblog.www.travelhub.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {

    Button traveller,travelAgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        traveller = (Button)findViewById(R.id.travellerBtn);
        travelAgency = (Button)findViewById(R.id.travelAgencyBtn);

        traveller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,TravellerSignUpActivity.class));
            }
        });

        travelAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(SignUpActivity.this,"This feature is not available yet",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
