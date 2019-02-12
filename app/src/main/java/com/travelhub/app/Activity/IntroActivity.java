package com.travelhub.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.travelhub.app.R;

public class IntroActivity extends AppCompatActivity {

    Button signin,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        signin = findViewById(R.id.introSignin);
        signup = findViewById(R.id.introSignup);

        SharedPreferences userPref = getSharedPreferences(
                getString(R.string.user_data), Context.MODE_PRIVATE);
        String userData = userPref.getString(getString(R.string.login_user_data),"");
        //editor.putString(getString(R.string.login_user_data), response.body().getAsString());

        if (userData==null || !userData.equals("")) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(IntroActivity.this,LoginActivity.class));
                //finish();

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this,SignupActivity.class));
                //finish();
            }
        });
    }
}
