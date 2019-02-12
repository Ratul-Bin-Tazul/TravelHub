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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.travelhub.app.ApiClient.ApiClient;
import com.travelhub.app.ApiInterface.ApiInterface;
import com.travelhub.app.DataModel.LoginResonseData;
import com.travelhub.app.DataModel.RegisterResonseData;
import com.travelhub.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WritePostActivity extends AppCompatActivity {

    EditText story;
    
    Button post;
    private ProgressDialog progressDialog;

    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        
        story = findViewById(R.id.writePostStory);
        username = findViewById(R.id.writePostUsername);
        
        post = findViewById(R.id.writePostPostButton);

        progressDialog = new ProgressDialog(this);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String userData = sharedPref.getString(getString(R.string.login_user_data), "");

        Gson gson = new Gson();
        LoginResonseData data = gson.fromJson(userData,LoginResonseData.class);

        username.setText(data.getUsername());

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postStory(data.getId(),data.getUsername(),data.getProfilePicture(),story.getText().toString(),"","public");
            }
        });
    }

    private void postStory(String uid,String uname,String uPic,String postText,String photoes,String privacy) {

        progressDialog.setMessage("Posting your story...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<RegisterResonseData> loginResult = apiService.postStory(uid,uname,uPic,postText,photoes,privacy);

        loginResult.enqueue(new Callback<RegisterResonseData>() {
            @Override
            public void onResponse(Call<RegisterResonseData> call, Response<RegisterResonseData> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e("res", "res login: " + response.raw());
                Log.e("res", "res login: " + response.body().getError());


                if (!response.body().getError()) {

                    Toast.makeText(WritePostActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(WritePostActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
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

}
