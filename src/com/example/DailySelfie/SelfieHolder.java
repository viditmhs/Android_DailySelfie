package com.example.DailySelfie;

/**
 * Created by IntelliJ IDE
 * User : vidit
 * Date : 11/26/14.
 * Time : 7:30 PM
 * Contact id; vidit.maheshwari@gmail.com
 * To modify this template follow File->Settings->File and Code Templates->Includes
 */
import android.graphics.Bitmap;

public class SelfieHolder {

    // Scaled image holder
    private Bitmap mImage;

    // Image path holder
    private String mPath;

    public SelfieHolder(Bitmap image, String path){

        mImage = image;
        mPath = path;
    }

    public Bitmap getImage(){

        return mImage;
    }

    public String getPath(){

        return mPath;
    }

}
