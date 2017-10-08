package org.firstinspires.ftc.teamcode;

/**
 * Created by Luxon on 9/19/2017.
 */
/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.lang.Math.*;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="ControlOperation", group="Linear Opmode")
public class DrivingMain extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    Hardware robot=new Hardware();
    
    //Ex Output: mod(-9,4) -> 3
    public int mod(int num, int div) {
        return num - (int)Math.floor((float)num/div)*div;
    }

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        
        //Zero the heading
        telemetry.addData("Status", "Calibrating the Gyro, Don't Move...");
        telemetry.update();
        robot.gyro.calibrate();
        telemetry.addData("Status", "Calibration Finished!");
        telemetry.update();

        boolean[] pressed = new boolean[2];
        boolean omniMode = true;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double rfPower = 0, rbPower = 0, lfPower = 0, lbPower = 0;
            double xl = gamepad1.left_stick_x, yl = -gamepad1.left_stick_y; //moving thumbstick up results in -y value
            double xr = gamepad1.right_stick_x;

            //toggles driving mode
            if (gamepad1.a && !pressed[0]) {
                omniMode = !omniMode;
                pressed[0] = true;
            } else if (!gamepad1.a) {
                pressed[0] = false;
            }
            //Reorients Robot (see reOrient() for more info.)
            if (gamepad1.y && !pressed[1]) {
                reOrient();
                pressed[1] = true;
            } else if (!gamepad1.y) {
                pressed[1] = false;
            }

            //Calculates the power to give each motor for the two driving modes based on thumbstick position
            if (omniMode) {
                rfPower = -xl + yl;
                rbPower = xl + yl;
                lfPower = xl + yl;
                lbPower = -xl + yl;
            }
            else {
                if(Math.abs(xl) >= Math.abs(yl)){ //if the thumbstick is facing more horizontally than vertically
                    rfPower = -xl;
                    rbPower = xl;
                    lfPower = xl;
                    lbPower = -xl;
                }
                else{
                    rfPower = yl;
                    rbPower = yl;
                    lfPower = yl;
                    lbPower = yl;
                }
            }

            //Changes motors' power to account for turning
            rfPower -= xr;
            rbPower -= xr;
            lfPower += xr;
            lbPower += xr;

            //Divides each motors' power by the highest power of the four motors to keep all powers within the setPower() method's parameters (-1<=power<=1)
            double max = Math.max(Math.max(Math.max(Math.abs(rfPower), Math.abs(rbPower)), Math.abs(lfPower)), Math.abs(lbPower));
            rfPower /= max;
            rbPower /= max;
            lfPower /= max;
            lbPower /= max;

            robot.rightMotorF.setPower(rfPower);
            robot.rightMotorB.setPower(rbPower);
            robot.leftMotorF.setPower(lfPower);
            robot.leftMotorB.setPower(lbPower);
            
            idle();
        }
    }

    //Reorients the Robot so it faces the shelves (or the nearest multiple of 90/ cardinal direction in relation to the initial header)
    public void reOrient() {
        float curHeading = (float)robot.gyro.getHeading();
        //target heading is from 0-360 (0,90,180,270,360)
        int targetHeading = (int)(curHeading/90 + 0.5) * 90;
        
        //Turns robot towards Target Heading (added last part just in case Cur==Target @ 0)
        //Multiplied target heading by 90 to allow all cases to work (if curHeading>targetHeading && targetHeading==1, then the robot would've moved CCW)
        if ((mod(targetHeading - (int)curHeading + 180, 360) - 180) < 0) {//if(curHeading < (targetHeading == 0 ? 360 : targetHeading && curHeading != targetHeading)){ this will not always take the shortest route to the target
                robot.rightMotorB.setPower(.5); //test and set to power that'll ensure greatest accuracy:speed ratio
                robot.rightMotorF.setPower(.5);
                robot.leftMotorF.setPower(-.5);
                robot.leftMotorB.setPower(-.5);
        }
        else if(curHeading != targetHeading) {//else if(curHeading > targetHeading){
                robot.rightMotorB.setPower(-.5); //test and set to power that'll ensure greatest accuracy:speed ratio
                robot.rightMotorF.setPower(-.5);
                robot.leftMotorF.setPower(.5);
                robot.leftMotorB.setPower(.5);
        }
        //when the loop breaks, the robot is at the targetHeading
        while (robot.gyro.getHeading() != (targetHeading==360 ? 0 : targetHeading))
        {
            telemetry.addData("Status", "Reorienting... Please Wait...");
            telemetry.update();
        }
        robot.rightMotorB.setPower(0);
        robot.rightMotorF.setPower(0);
        robot.leftMotorF.setPower(0);
        robot.leftMotorB.setPower(0);
        telemetry.addData("Status","Done!");
        telemetry.update();
    }
}
//https://gist.github.com/jboulhous/6007980

/*
git add .
git commit -m "[Insert Commit Here]"

git push -u origin master

*/