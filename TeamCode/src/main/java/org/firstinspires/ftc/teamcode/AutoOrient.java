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

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;

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

@Autonomous(name="AutoOrient", group="Linear Opmode")
public class AutoOrient extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    Hardware robot=new Hardware();

    private int mod(int num, int div) {
        return num - (int)Math.floor((float)num/div)*div;
    }

    private int angleDifference(int a1, int a2){
        return mod(a1 - a2 + 180, 360) - 180;
    }

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //Zero the heading
        telemetry.addData("Status", "Calibrating the Gyro, Don't Move...");
        telemetry.update();
        robot.gyro.calibrate();
        telemetry.addData("Status", "Calibration Finished!");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        //Zero the heading

        // run until the end of the match (driver presses STOP)

        robot.rightMotorB.setPower(-turnPower);
        robot.rightMotorF.setPower(-turnPower);
        robot.leftMotorF.setPower(turnPower);
        robot.leftMotorB.setPower(turnPower);
        while(opModeIsActive() && runtime.seconds() < 1){
            telemetry.addData("Status", "Gyro Heading: " + robot.gyro.getHeading());
            telemetry.update();
            idle();
        }

        robot.rightMotorB.setPower(0);
        robot.rightMotorF.setPower(0);
        robot.leftMotorF.setPower(0);
        robot.leftMotorB.setPower(0);


        sleep(1500);
        reOrient();
        sleep(3000);

            //Calculates the power to give each motor for the two driving modes based on thumbstick position



            //open and close grabber

    }

    //Reorients the Robot so it faces the shelves (or the nearest multiple of 90/ cardinal direction in relation to the initial header)
    private float turnPower = .08f;
    private void reOrient() {
        int curHeading = robot.gyro.getHeading();
        telemetry.addData("Heading", curHeading);
        telemetry.update();
        //target heading is from 0-360 (0,90,180,270,360)
        int targetHeading = (int)(curHeading/90.0 + 0.5) * 90;
        if(targetHeading==360){
            targetHeading=0;
        }
        telemetry.addData("Status","Angle Difference" + angleDifference(targetHeading ,  robot.gyro.getHeading()));
        telemetry.update();
        sleep(2000);
        //Turns robot towards Target Heading (added last part just in case Cur==Target @ 0)
        //Multiplied target heading by 90 to allow all cases to work (if curHeading>targetHeading && targetHeading==1, then the robot would've moved CCW)

        if (angleDifference(targetHeading, curHeading) < 0) {//if(curHeading < (targetHeading == 0 ? 360 : targetHeading && curHeading != targetHeading)){ this will not always take the shortest route to the target
                robot.rightMotorB.setPower(-turnPower); //test and set to power that'll ensure greatest accuracy:speed ratio
                robot.rightMotorF.setPower(-turnPower);
                robot.leftMotorF.setPower(turnPower);
                robot.leftMotorB.setPower(turnPower);
        }
        else if(curHeading != targetHeading) {//else if(curHeading > targetHeading){
                robot.rightMotorB.setPower(turnPower); //test and set to power that'll ensure greatest accuracy:speed ratio
                robot.rightMotorF.setPower(turnPower);
                robot.leftMotorF.setPower(-turnPower);
                robot.leftMotorB.setPower(-turnPower);
        }
        //when the loop breaks, the robot is at the targetHeading
        curHeading = robot.gyro.getHeading();
        while (Math.abs(angleDifference(targetHeading, curHeading)) > 5)
        {
            curHeading = robot.gyro.getHeading();
            telemetry.addData("Status", "Gyro Heading: " + curHeading);
            telemetry.addData("Status", "Reorienting... Please Wait...");
            telemetry.update();
        }

        robot.rightMotorB.setPower(0);
        robot.rightMotorF.setPower(0);
        robot.leftMotorF.setPower(0);
        robot.leftMotorB.setPower(0);
        sleep(1500);
        curHeading = robot.gyro.getHeading();
        telemetry.addData("Heading", curHeading);
        telemetry.update();
        sleep(3000);
    }
}
//https://gist.github.com/jboulhous/6007980

/*
git add .
git commit -m "[Insert Commit Here]"

git push -u origin master
//(curHeading > targetHeading + 1 || curHeading < (targetHeading==0 ? 359 : targetHeading - 1))
*/