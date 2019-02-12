package com.bdtravelblog.www.travelhub.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bdtravelblog.www.travelhub.R;
import com.bdtravelblog.www.travelhub.Utilities.FirebaseReferenceNames;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class TravellerSignUpActivity extends AppCompatActivity {


    private final int SIGN_UP = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_sign_up);


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                ))
                        .setAllowNewEmailAccounts(true)
                        .setTheme(R.style.GreenTheme)
                        .build(),
                SIGN_UP);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == SIGN_UP) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                Toast.makeText(TravellerSignUpActivity.this,FirebaseAuth.getInstance().getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TravellerSignUpActivity.this,SignUpProfileActivity.class));
                finishAffinity();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button

                    Toast.makeText(TravellerSignUpActivity.this,R.string.sign_in_cancelled,Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(TravellerSignUpActivity.this,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();

                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(TravellerSignUpActivity.this,R.string.unknown_error,Toast.LENGTH_SHORT).show();

                    return;
                }
            }

            Toast.makeText(TravellerSignUpActivity.this,R.string.unknown_sign_in_response,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
