package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Luxon on 3/4/2017.
 */

public class Tween {

    private ElapsedTime runtime;

    public Tween(ElapsedTime rTime) {
        runtime = rTime;
    }

    public float tweened(double sTime, double eTime, float sVal, float eVal) {
        float vDif = eVal - sVal;
        double tDif = eTime - sTime;
        float alpha = (float)((runtime.seconds() - sTime) / tDif);
        return sVal + vDif*alpha;
    }

}
