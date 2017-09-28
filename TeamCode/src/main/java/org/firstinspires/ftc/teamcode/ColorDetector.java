package org.firstinspires.ftc.teamcode;

/**
 * Created by TheBARYONS on 2/7/2017.
 */
//import android.Manifest;
import android.content.Context;
//import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
//import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by chuck.spohr on 2/5/2017.
 */

public class ColorDetector {
    private Context mContext;
    private Camera mCamera;
    private ColorDetectorListener mColorDetectorListener;

    public ColorDetector(Context context) {
        mContext = context;
    }

    public interface ColorDetectorListener {
        public void onColorInfoReady(ColorInfo colorInfo, Bitmap bitmap);
    }

    public void setColorDetectorListener(ColorDetectorListener listener)
    {
        mColorDetectorListener = listener;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            releaseCamera();
            Bitmap rawbmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bmp = RotateBitmap(rawbmp, 90);
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] bmpPixels = new int[width * height];
            bmp.getPixels(bmpPixels, 0, width, 0, 0, width, height);

            ColorInfo colorInfo = analyzePixels(bmpPixels, width, height);

            mColorDetectorListener.onColorInfoReady(colorInfo, bmp);
        }
    };

    public void Detect() {

//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(mContext, "No permission", Toast.LENGTH_SHORT).show();
//            return;
//        }

        mCamera = Camera.open();
        Camera.Parameters cameraParameters = mCamera.getParameters();
        cameraParameters.setPreviewFormat(ImageFormat.NV21);

        Camera.Size preferredSize = getPreferredSize(cameraParameters.getSupportedPictureSizes());
        cameraParameters.setPictureSize(preferredSize.width, preferredSize.height);

        mCamera.setDisplayOrientation(0);
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(10));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.setParameters(cameraParameters);
        cameraParameters = mCamera.getParameters();

        mCamera.startPreview();
        mCamera.takePicture(null, null, mPictureCallback);
    }

    private ColorInfo analyzePixels(int[] pixels, int width, int height) {
        long redTotal = 0;
        long blueTotal = 0;

        long redBiasTotal = 0;
        long blueBiasTotal = 0;

        double totalRedMomentX = 0;
        double totalBlueMomentX = 0;

        ColorInfo colorInfo = new ColorInfo();

        for(int row = height/2; row < height; row++) {
            for (int col = 0; col < width; col ++) {
                int i = row * width + col;
                redTotal = redTotal + Color.red(pixels[i]);
                blueTotal = blueTotal + Color.blue(pixels[i]);

                redBiasTotal = redBiasTotal + Color.red(pixels[i]) - Color.blue(pixels[i]);
                blueBiasTotal = blueBiasTotal + Color.blue(pixels[i]) - Color.red(pixels[i]);

                totalRedMomentX = totalRedMomentX + ((Color.red(pixels[i]) - Color.blue(pixels[i]) * (col - (width/2))));
                totalBlueMomentX = totalBlueMomentX + ((Color.blue(pixels[i]) - Color.red(pixels[i]) * (col - (width/2))));
            }
        }
        colorInfo.AvgRed = (int) (redTotal / (width * height));
        colorInfo.AvgBlue = (int) (blueTotal / (width * height));
        colorInfo.AvgRedBias = (int) (redBiasTotal / (width * height));
        colorInfo.AvgBlueBias = (int) (blueBiasTotal / (width * height));

        if (colorInfo.AvgRedBias > colorInfo.AvgBlueBias) {
            colorInfo.PredominantColor = "Red";
            colorInfo.isRed = true;
        } else if (colorInfo.AvgBlueBias > colorInfo.AvgRedBias) {
            colorInfo.PredominantColor = "Blue";
            colorInfo.isRed = false;
        } else {
            colorInfo.PredominantColor = "Unknown";
            colorInfo.isRed = false;
        }

        colorInfo.RedMomentX = (float) (totalRedMomentX / (width * height));
        colorInfo.BlueMomentX = (float) (totalBlueMomentX / (width * height));

        colorInfo.RedOnLeft = (colorInfo.RedMomentX < colorInfo.BlueMomentX);

        colorInfo.PictureSize = String.format("width: %d height: %d", width, height);

        return colorInfo;
    }

    private Camera.Size getPreferredSize(List<Camera.Size> sizes) {
        Camera.Size retSize = sizes.get(0);
        int minWidth = 30000;
        for (Camera.Size option : sizes) {
            if(option.width < minWidth) {
                minWidth = option.width;
                retSize = option;
            }
        }
        return retSize;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
