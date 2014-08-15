package com.example.adamcrawford.locpic;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements LocationListener {

    public static String TAG = "MainActivity";
    ImageView picView;
    String photoStore;
    Uri fileUri;
    File photoFile;
    Intent battStatus;
    LocationManager lManager;
    Criteria criteria;
    float bPct;
    boolean chg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picView = (ImageView) findViewById(R.id.picView);

        Button capture = (Button) findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePic();
            }
        });
    }

    private void getLoc(){

        Log.i(TAG, "In getloc");

        if (lManager != null){

            Log.i(TAG, "lManager exists");

            // Normal updates while activity is visible.
            lManager.requestLocationUpdates(2*1000, 0, criteria, this, null);
            //lManager.requestLocationUpdates("gps", 2*1000, 0, this);


            // Register a receiver that listens for when a better provider than I'm using becomes available.
            String bestProvider = lManager.getBestProvider(criteria, false);
            String bestAvailableProvider = lManager.getBestProvider(criteria, true);
            if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
                Log.i(TAG, "better provider");
               lManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener, getMainLooper());
            }
        }
    }

    protected LocationListener bestInactiveLocationProviderListener = new LocationListener() {
        public void onLocationChanged(Location l) {}
        public void onProviderDisabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "Better accuracy");
            getLoc();
        }
    };

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "LAT: " + String.valueOf(location.getLatitude()));
        Log.i(TAG, "LON: " + String.valueOf(location.getLongitude()));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i(TAG, "Status Changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "Provider Enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "Disabled");
    }

    private float battPct(){
        int level;
        int scale;
        if (battStatus != null) {
            level = battStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = battStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            return (level / (float)scale)*100;
        }
        return -1;
    }

    private boolean isCharging(){
        int charging = this.battStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return charging == BatteryManager.BATTERY_STATUS_CHARGING || charging == BatteryManager.BATTERY_STATUS_FULL;
    }

    private void takePic() {
        Intent capturePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (capturePic.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Log.e(TAG, e.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                capturePic.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(capturePic, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            fileUri = Uri.fromFile(photoFile);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            mediaScanIntent.setData(fileUri);
            this.sendBroadcast(mediaScanIntent);
            picView.setImageURI(fileUri);
        } else {
            Log.e(TAG, "data is null!");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        Log.e(TAG, timeStamp);
        String imageFileName = "LocPic_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        photoStore = "file:" + image.getAbsolutePath();
        return image;
    }

    private void printToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onPause() {
        if (lManager != null) {
            lManager.removeUpdates(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        lManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        battStatus = this.registerReceiver(null, filter);

        bPct = battPct();
        chg = isCharging();

        Log.e(TAG, "Batt%: " + bPct);
        Log.e(TAG, "Charging: " + chg);

        if (bPct != -1){
            if (bPct > 25.0 || isCharging()) {
                //gtr than qtr
                Log.i(TAG, "Getting loc");
                getLoc();
            } else {
                //qtr or less
                //Disabled Location
                printToast(getString(R.string.low));
            }
        } else {
            //Could not get batt%
            printToast(getString(R.string.noStatus));
        }

        super.onResume();
    }
}
