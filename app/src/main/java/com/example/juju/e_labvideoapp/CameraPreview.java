package com.example.juju.e_labvideoapp;


import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.graphics.Canvas;
import android.graphics.Paint;


class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";
    SurfaceHolder mHolder = this.getHolder();
    public Camera camera;

    CameraPreview(Context context) {
        super(context);
        Log.d("Preview", "CameraPreview constractor");
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
        this.camera = this.openBackFacingCamera();
        //this.camera.setDisplayOrientation(90);
    }

    String timeStampFileP;

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Preview", "SurfaceCreated ");

        timeStampFileP = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/hmi/");
        wallpaperDirectory.mkdirs();

        File wallpaperDirectory1 = new File(Environment.getExternalStorageDirectory().getPath()+"/hmi/"+timeStampFileP);
        wallpaperDirectory1.mkdirs();

        try {
            this.camera.setPreviewDisplay(holder);
            this.camera.setPreviewCallback(new PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera arg1) {
                    FileOutputStream outStream = null;
                    int previewCount = 0;

                    if (previewCount < 10) {
                    try {
                        ++previewCount;
                        Log.d("Preview", "Start preview outstream write ");
                        outStream = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath() + "/hmi/" + timeStampFileP + "/preview_%d.bmp", new Object[]{Long.valueOf(System.currentTimeMillis())}));
                        outStream.write(data);
                        outStream.close();
                        Log.d("Preview", "onPreviewFrame - wrote bytes: " + data.length);
                    } catch (FileNotFoundException var5) {
                        var5.printStackTrace();
                    } catch (IOException var6) {
                        var6.printStackTrace();
                    }
                    }
                    else{
                        previewCount = 0;
                        CameraPreview.this.invalidate();
                    }
                }
            });
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.camera.stopPreview();
        this.camera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.camera.startPreview();
    }

    private Camera openBackFacingCamera() {
        Camera cam = null;
        CameraInfo cameraInfo = new CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();

        for(int camIdx = 0; camIdx < cameraCount; ++camIdx) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if(cameraInfo.facing == 0) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException var6) {
                    Log.e("Preview", "Camera failed to open: " + var6.getLocalizedMessage());
                }
            }
        }

        return cam;
    }
}
