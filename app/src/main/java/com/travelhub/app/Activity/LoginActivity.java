package com.travelhub.app.Activity;

import android.app.Dialog;
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

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    EditText email,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        loginButton = findViewById(R.id.loginButton);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    login(email.getText().toString() , password.getText().toString());
                }else {
                    Toast.makeText(LoginActivity.this,"Please fill out all the necessary information!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email,String password) {

        progressDialog.setMessage("Accessing your travel backpack...");
        progressDialog.setCancelable(false);
        progressDialog.show();

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

                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,loginResonseData.getMessage(),Toast.LENGTH_SHORT).show();
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
