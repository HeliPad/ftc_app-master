package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;


/**
 * Created by Luxon on 10/15/2016.
 */


public class Shooter {


    //public DcMotor shootMotor = null;
    public Gamepad gamepad = null;
    //public DcMotor sweepCMotor = null;
    //public Servo shootServo = null;
    public int powerLevel = 10;
    public double powerLevelCS = 1;
    public boolean isInversed = false;
    public boolean activatedSh = false;
    public boolean activatedCM = false;
    Hardware robot;

    public Shooter() {


    }


    public void init(Hardware hw, Gamepad gp) {
        robot   = hw;
        gamepad = gp;
    }

    private boolean[] pressed = new boolean[5];

    //0 is dpad_up and dpad_down
    //1 is x
    //2 is b
    //3 is y
    //4 is x
    public void step() {
        if (gamepad.dpad_up && !pressed[0]) {
            pressed[0] = true;
            if (powerLevel < 10) {
                powerLevel += 2;
            }
        } else if (gamepad.dpad_down && !pressed[0]) {
            pressed[0] = true;
            if (powerLevel > 0) {
                powerLevel -= 2;
            }
        } else if (!gamepad.dpad_up && !gamepad.dpad_down) {
            pressed[0] = false;
        }

        if (gamepad.x && !pressed[1]) {
            activatedSh = !activatedSh;
            pressed[1] = true;
        } else if (!gamepad.x) {
            pressed[1] = false;
        }

        if (gamepad.right_bumper && !pressed[3]) {
                powerLevelCS = 0;
                pressed[3] = true;
        } else if (!gamepad.right_bumper && pressed[3]) {
            if (isInversed) {
                powerLevelCS = 1;
                pressed[3] = false;
                isInversed = false;
            } else {
                powerLevelCS = -1;
                pressed[3] = false;
                isInversed = true;
            }
        }

        if (gamepad.y && !pressed[2]) {
            activatedCM = !activatedCM;
            pressed[2] = true;
        } else if (!gamepad.y) {
            pressed[2] = false;
        }

        /*if (gamepad.x && !pressed[4]) {
            double pos= shootServo.getPosition();
            if(pos+.25>.75)
                shootServo.setPosition(.25);
            else
                shootServo.setPosition(pos + .25);
            pressed[4] = true;
        } else if (!gamepad.x) {
            pressed[4] = false;
        }*/

        //robot.sweepMotor.setPower(activatedCM ? -powerLevelCS : 0);
        //robot.shootMotor.setPower(activatedSh ? -powerLevel * 0.1f : 0);
    }
    public void setInverse(boolean g){
        isInversed=g;
    }
}


