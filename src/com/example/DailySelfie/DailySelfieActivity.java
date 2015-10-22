package com.example.DailySelfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.app.ActionBar;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailySelfieActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath = "None";

    SelfieViewAdapter mAdapter;

    final String info = "INFO::";
    ArrayList<SelfieHolder> selfieHolderArrayList = null;

    private final static String fileName = "File.txt";

    private AlarmManager mAlarmManager;

    // Alaram and notification related types
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;
    private static final long INITIAL_ALARM_DELAY = 2*60* 1000L;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adding Action Bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        // Displaying the action bar
        ActionBar actionBar = getActionBar();
        actionBar.show();
        Log.i(info, "ActionBar activated");


        //Creating the data storage
        selfieHolderArrayList = new ArrayList<SelfieHolder>();

        //Extracting previous selfies taken
            // Checking if file exist else start extracing all the selfies
        if (!getFileStreamPath(fileName).exists()) {
            Log.i(info, fileName + " doesn't exist");
           try {
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                PrintWriter pw = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(fos)));
                pw.close();
            } catch (FileNotFoundException e) {
                Log.i(info, "FileNotFoundException");
            }
        }else{

            Log.i(info, fileName+" already exist");
            ArrayList<String> filePathList = new ArrayList<String>();
            try {
                getAllFilePaths(filePathList);
            }catch (IOException e){
                Log.e("ERROR::","IOException");
            }

            setData(selfieHolderArrayList, filePathList);
            filePathList.clear();

        }

        //Creating my adapter
        mAdapter = new SelfieViewAdapter(this, R.layout.selfie_list, selfieHolderArrayList);

        //Creating ListView
        ListView listView = getListView();

        // Adding Adapter to ListView
        listView.setAdapter(mAdapter);

        //Get the Alarm manager
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(DailySelfieActivity.this,
				DailySelfieAlaramNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                DailySelfieActivity.this, 0, mNotificationReceiverIntent, 0);

        // Implementing setOnItemClickedListener()
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(info, "ListView.setOnItemClicked called");

                TextView textView = (TextView)view.findViewById(R.id.filename);
                String loc = textView.getText().toString();
                Intent startPictureView = new Intent(getApplicationContext(), DailySelfieImageView.class);
                startPictureView.putExtra("FilePath", loc);
                startActivity(startPictureView);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        Log.i(info, "OnCreateOptionsMenu called");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){

            case R.id.camera:

                Log.i(info, "OnOptionsItemSelected called. Calling dispatchTakePictureIntent");
                dispatchTakePictureIntent();
                return true;

            case R.id.setAlarm:

                Log.i(info, "OnOptionsItemSelected called. Setting Alarm ON");
                mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                        INITIAL_ALARM_DELAY,
                        mNotificationReceiverPendingIntent);
                Toast.makeText(getApplicationContext(), "Alarm is ON", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.cancelAlarm:

                Log.i(info, "OnOptionsItemSelected called. Setting Alarm OFF");
                mAlarmManager.cancel(mNotificationReceiverPendingIntent);
                Toast.makeText(getApplicationContext(), "Alarm is OFF", Toast.LENGTH_SHORT).show();
                return true;

            default:
                Toast.makeText(getApplicationContext(), "None selected", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

    }

    // function to start a camera
    private void dispatchTakePictureIntent() {

        // Creating an intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensuring that camera activity can be handled
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Creating a file where photo will stored
            File photoFile = null;

            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                Log.e("Error", ex.getMessage());
                Toast.makeText(getApplicationContext(), "Error at creatingFileName", Toast.LENGTH_LONG).show();
            }

            if(photoFile != null){

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(info, "OnActivityResult called");

        galleryAddPic();

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bitmap = setPic(mCurrentPhotoPath);

            if(bitmap != null) {
                mAdapter.add(new SelfieHolder(bitmap, mCurrentPhotoPath));
                Log.i(info, "New SelfieHolder added to adapter");

                // Have to add this path to our Local storage file
                try {
                    writeFile(mCurrentPhotoPath);
                } catch (IOException e) {
                    Log.e("ERROR::", "IOException");
                }
            }else{

                Log.i(info, "On onActivityResult method bitmap is null");
            }
        }
    }

    // method to generate file and filepath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.i(info, "filePathName "+mCurrentPhotoPath);

        return image;
    }

    // Adding photos to media database
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // Scaling the image
    private Bitmap setPic(String filePath) {
        // Get the dimensions of the View

        final float scale = getResources().getDisplayMetrics().density;

        int targetW = (int)(160*scale);
        int targetH = (int)(120*scale);

            // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

           // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        return bitmap;
    }

    private void writeFile(String line) throws  IOException{
        FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);

        OutputStreamWriter osw = new OutputStreamWriter(fos);

        osw.write(line+"\n");
        osw.flush();
        osw.close();

    }

    private void getAllFilePaths(ArrayList<String> list) throws IOException{

        list.clear();

        FileInputStream fis = openFileInput(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = "";

		while (null != (line = br.readLine())) {

            list.add(line);
        }

		br.close();
    }

    private void setData(ArrayList<SelfieHolder> selfieholderlist, ArrayList<String> filePathlist){


        for(String path : filePathlist){

            Bitmap bitmap= setPic(path);

            selfieholderlist.add(new SelfieHolder(bitmap, path));

        }
    }
    @Override
    public void onStop(){
        super.onStop();

        ArrayList<String> list = new ArrayList<String>();
        try {
            getAllFilePaths(list);
        }catch (IOException e){
            Log.e("ERROR::", "In onStop got IOException");
        }

        for(String e : list){

            Log.i(info , e);
        }

    }

}
