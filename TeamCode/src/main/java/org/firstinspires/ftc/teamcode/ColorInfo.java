package org.firstinspires.ftc.teamcode;
/**
 * Created by TheBARYONS on 2/7/2017.
 */
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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



public class ColorInfo {
    public int AvgRed = 0;
    public int AvgBlue = 0;
    public int AvgRedBias = 0;
    public int AvgBlueBias = 0;
    public String PredominantColor = "";
    public float RedMomentX = 0;
    public float BlueMomentX = 0;
    public boolean RedOnLeft = false;
    public String PictureSize = "";

    public boolean isRed = false;

    public String toString() {
        return String.format("AvgRed: %d\n" +
                        "AvgBlue: %d\n" +
                        "AvgRedBias: %d\n" +
                        "AvgBlueBias: %d\n" +
                        "PredominantColor: %s\n" +
                        "RedMomentX: %.2f\n" +
                        "BlueMomentX: %.2f\n" +
                        "RedOnLeft: %b\n" +
                        "PictureSize: %s",
                AvgRed, AvgBlue, AvgRedBias, AvgBlueBias, PredominantColor, RedMomentX, BlueMomentX, RedOnLeft, PictureSize);
    }
}


