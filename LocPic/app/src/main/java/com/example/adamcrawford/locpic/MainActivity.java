package com.example.adamcrawford.locpic;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    public static String TAG = "MainActivity";
    Camera cam;
    int camCount;
    SurfaceHolder holder;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button stopButton = (Button) findViewById(R.id.stop);


        surfaceView = (SurfaceView) findViewById(R.id.camPrev);
        holder = surfaceView.getHolder();
        holder.addCallback(this);


        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        camCount = Camera.getNumberOfCameras();

        for (int curCam = 0; curCam < camCount; curCam++){
            Camera.getCameraInfo(curCam, camInfo);
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cam = getCameraInstance(curCam);
            }
        }

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cam != null) {
                    cam.release();
                    cam = null;
                }
            }
        });

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cam != null) {
                    try {
                        cam.setPreviewDisplay(holder);
                        cam.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
