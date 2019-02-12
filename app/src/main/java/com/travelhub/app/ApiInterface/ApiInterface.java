package com.travelhub.app.ApiInterface;

import com.google.gson.JsonObject;
import com.travelhub.app.DataModel.LoginResonseData;
import com.travelhub.app.DataModel.RegisterResonseData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {

    //Register
    @FormUrlEncoded
    @POST("register")
    Call<RegisterResonseData> register(@Field("username") String username, @Field("fullname") String fullname,@Field("email") String email, @Field("password") String password);

    //Register
    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> login(@Field("email") String email, @Field("password") String password);

    //post story
    @FormUrlEncoded
    @POST("createPost")
    Call<RegisterResonseData> postStory(@Field("user_id") String user_id, @Field("username") String username, @Field("user_picture") String user_picture
            , @Field("post_text") String post_text, @Field("post_photos") String post_photos, @Field("privacy") String privacy);

}