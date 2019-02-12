package com.bdtravelblog.www.travelhub.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import static java.security.AccessController.getContext;

/**
 * Created by SAMSUNG on 11/16/2017.
 */

public class CropImageActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start picker to get image for cropping and then use the image in cropping activity
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);



    }
}
