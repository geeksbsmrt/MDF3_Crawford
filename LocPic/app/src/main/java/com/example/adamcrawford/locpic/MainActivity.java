package com.example.adamcrawford.locpic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    public static String TAG = "MainActivity";
    Camera cam;
    int camCount;
    SurfaceHolder holder;
    SurfaceView surfaceView;
    Camera.CameraInfo camInfo;
    int rearCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button capture = (Button) findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cam != null){
                    cam.takePicture(null, null, pictureCallback);
                }
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.camPrev);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        camInfo = new Camera.CameraInfo();
        camCount = Camera.getNumberOfCameras();

        for (int curCam = 0; curCam < camCount; curCam++){
            Camera.getCameraInfo(curCam, camInfo);
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                rearCam = curCam;
            }
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int facing){
        Camera cam = null;
        try {
            cam = Camera.open(facing); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(TAG, e.getMessage());
        }
        return cam; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        cam = getCameraInstance(rearCam);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (cam != null) {
            Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            if(display.getRotation() == Surface.ROTATION_0)
            {
                Log.i(TAG, "0");
                cam.setDisplayOrientation(90);
            }

            if(display.getRotation() == Surface.ROTATION_90)
            {
                Log.i(TAG, "90");
            }

            if(display.getRotation() == Surface.ROTATION_180)
            {
                Log.i(TAG, "180");
            }

            if(display.getRotation() == Surface.ROTATION_270)
            {
                Log.i(TAG, "270");
                cam.setDisplayOrientation(180);
            }

            try {
                cam.setDisplayOrientation(90);
                cam.setPreviewDisplay(surfaceHolder);
                cam.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        if (cam != null) {
//            cam.stopPreview();
//            cam.release();
//            cam = null;
//        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
}
