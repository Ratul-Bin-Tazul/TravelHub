package com.travelhub.app.Activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.travelhub.app.ApiClient.ApiClient;
import com.travelhub.app.ApiInterface.ApiInterface;
import com.travelhub.app.DataModel.LoginResonseData;
import com.travelhub.app.DataModel.RegisterResonseData;
import com.travelhub.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText username,fullname,email,password;

    private Button signupButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);

        signupButton = findViewById(R.id.signupButton);

        username = findViewById(R.id.signupUsername);
        fullname = findViewById(R.id.signupFullname);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!username.getText().toString().equals("") && !fullname.getText().toString().equals("") &&
                        !email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    register(username.getText().toString() , fullname.getText().toString() , email.getText().toString() , password.getText().toString());
                }else {
                    Toast.makeText(SignupActivity.this,"Please fill out all the necessary information!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(String uname,String fullname,String email,String password) {

        progressDialog.setMessage("Creating your travel backpack...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<RegisterResonseData> signupResult = apiService.register(uname,fullname,email,password);

        signupResult.enqueue(new Callback<RegisterResonseData>() {
            @Override
            public void onResponse(Call<RegisterResonseData> call, Response<RegisterResonseData> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e("res", "res signup: " + response.raw());
                Log.e("res", "res signup: " + response.body().getMessage());


                if (!response.body().getError()) {
                    login(email,password);
//                    startActivity(new Intent(SignupActivity.this,MainActivity.class));
//                    finish();
                }else {
                    Toast.makeText(SignupActivity.this,"Please fill out all the necessary information!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResonseData> call, Throwable t) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e("res", "err: " + t.toString());

            }
        });

    }

    private void login(String email,String password) {

        progressDialog.setMessage("Accessing your travel backpack...");
        progressDialog.setCancelable(false);
        //progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> loginResult = apiService.login(email,password);

        loginResult.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();



                Gson gson = new Gson();

                LoginResonseData loginResonseData = gson.fromJson(response.body(),LoginResonseData.class);

                Log.e("res", "res login: " + response.raw());
                Log.e("res", "res login: " + loginResonseData.getError());


                if (!loginResonseData.getError()) {

                    SharedPreferences userPref = getSharedPreferences(
                            getString(R.string.user_data), Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString(getString(R.string.login_user_data), response.body().toString());
                    editor.apply();

                    Log.e("login saved", " " + response.body().toString());

                    startActivity(new Intent(SignupActivity.this,MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignupActivity.this,loginResonseData.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e("res", "err: " + t.toString());

            }
        });

    }

}
