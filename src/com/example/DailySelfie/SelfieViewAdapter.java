package com.example.DailySelfie;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.DailySelfie.SelfieHolder;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDE
 * User : vidit
 * Date : 11/26/14.
 * Time : 10:35 PM
 * Contact id; vidit.maheshwari@tmc.nl
 * Copy Right: All copy rights are hold by TMC Embedded BV
 * To modify this template follow File->Settings->File and Code Templates->Includes
 */
public class SelfieViewAdapter extends ArrayAdapter<SelfieHolder> {

    Context mContext;
    int mlayoutId;

    ArrayList<SelfieHolder> selfieHolderArrayList = null;

    public SelfieViewAdapter(Context context, int layoutId, ArrayList<SelfieHolder> data){
        super(context, layoutId, data);
        mContext = context;
        mlayoutId = layoutId;
        selfieHolderArrayList = data;
    }

    static class ViewHolder
    {
        ImageView image;
        TextView filePath;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(mlayoutId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.scaledImage);
            holder.filePath = (TextView)convertView.findViewById(R.id.filename);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        Bitmap image = selfieHolderArrayList.get(position).getImage();
        String path = selfieHolderArrayList.get(position).getPath();

        holder.image.setImageBitmap(image);
        holder.filePath.setText(path);

        return convertView;
    }


}
