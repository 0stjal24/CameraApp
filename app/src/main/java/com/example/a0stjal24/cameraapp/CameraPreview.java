package com.example.a0stjal24.cameraapp;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by 0stjal24 on 19/04/2018.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;

    public CameraPreview(Context ctx) {

        super(ctx);
        try {
            camera = Camera.open();
            this.getHolder().addCallback(this);
        }
        catch(Exception e) {
            Log.d("cameraApp", e.toString());
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (Exception e){
            Log.d("cameraApp", e.toString());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (camera != null) {

            boolean isPortrait =  getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            Camera.Parameters p= camera.getParameters();
            Camera.Size s = this.getClosestSize(p, width, height);
            if (s!=null) {
                camera.stopPreview();
                p.setPreviewSize(s.width, s.height);
                camera.setParameters(p);
                camera.startPreview();

                try {
                    camera.setPreviewDisplay(this.getHolder());
                } catch (IOException e) {
                    Log.e("cameraApp", "Error setting preview display: " + e);

                }

                // Have to rotate if portrait
                if(isPortrait) {
                    camera.setDisplayOrientation(90);
                }
            }
        }

    }

        // "w" and "h" are the current screen dimensions
    private Camera.Size getClosestSize(Camera.Parameters p,int w, int h) {

        Camera.Size s = null;

        // Get a list of all the supported preview dimensions
        List<Camera.Size> sizes = p.getSupportedPreviewSizes();

        int mindw=Integer.MAX_VALUE, dw;

        //aspectRatio = width divided by height

        double curRatio, aspectRatio = (double)w/(double)h, dratio, minDiffRatio = Double.MAX_VALUE;

        for(int i=0; i<sizes.size(); i++) {

            dw = Math.abs(sizes.get(i).width-w);

            curRatio = ((double)sizes.get(i).width) / ((double)sizes.get(i).height);

            // What is the difference between the aspect ratio of the screen and the aspect ratio of this dimension?
            dratio = Math.abs(curRatio-aspectRatio);

            // only consider if aspect ratio of this dimension is closest match so far
            // 0.0001 for possible rounding errors in double numbers

            if(dratio-0.0001 <= minDiffRatio ) {
                minDiffRatio  = dratio;

                // as we're selecting on aspect ratio we only need to consider one dimension (width in this example)
                // if the difference in width from the current width is the smallest so far,
                // then select this size
                if(dw < mindw) {
                    mindw = dw;

                    s = sizes.get(i);
                }
            }
        }

        return s;
    }





    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (camera != null) {
            try {
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                Log.d("cameraApp", e.toString());
            }
        }
    }
}
