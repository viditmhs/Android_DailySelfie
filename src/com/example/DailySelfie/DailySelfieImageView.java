package com.example.DailySelfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDE
 * User : vidit
 * Date : 11/28/14.
 * Time : 11:12 PM
 * Contact id; vidit.maheshwari@gmail.com
 * To modify this template follow File->Settings->File and Code Templates->Includes
 */
public class DailySelfieImageView extends Activity {

    private String mSelectedPhotoPath;
    private String info = "INFO::";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imageview);

        Bundle bundle = getIntent().getExtras();

        ImageView imgView = (ImageView)findViewById(R.id.seeImage);

        if(bundle.getString("FilePath") != null){

            mSelectedPhotoPath = bundle.getString("FilePath");
            Bitmap bitmap = BitmapFactory.decodeFile(mSelectedPhotoPath);
            if(bitmap != null) {
                imgView.setImageBitmap(bitmap);
            }else{
                Log.i(info, "Class:DailySelfieImageView, Method:onCreate() Bitmap is null.");
                finish();
            }

        }else{
            Log.i(info, "Class:DailySelfieImageView, Method:onCreate() ItemSelected has no file path.");
            finish();

        }

    }

}
