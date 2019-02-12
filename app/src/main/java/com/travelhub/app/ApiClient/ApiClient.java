package com.travelhub.app.ApiClient;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static final String BASE_URL = "https://gootrips.com/travelhub/";
    public static final String AUTH_USERNAME = "ratul38";
    public static final String AUTH_PIN = "323a66bab823525d";
    private static Retrofit retrofit = null;


//    public static String getAuthCredential() {
//        String base = USER
//        return "basic "+
//    }

    public static Retrofit getClient() {

        //okhttp client providing dev auth
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                //Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                // Credentials.basic(AUTH_USERNAME, AUTH_PIN));

                Request newRequest = originalRequest.newBuilder().build();
                return chain.proceed(newRequest);
            }
        }).build();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
