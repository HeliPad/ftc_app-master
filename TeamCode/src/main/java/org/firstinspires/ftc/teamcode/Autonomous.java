package org.firstinspires.ftc.teamcode;

/**
 * Created by Luxon on 10/03/2017.
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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

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

@Autonomous(name="Autonomous", group="Automus")
public class Autonomous extends LinearOpMode {
    /**
     * Steps for Autonomous:
     * 1. Extend Bar
     * 2. Use Color Sensor to Detect Jewel Color
     * 3. Hit Opposing Alliance Jewel w/ Bar
     * 4. Detract Bar
     * 5. Read Image w/ Vuforia and store the Key slot (1-3) in var. 
     * 6. Go off platform towards shelves
     * 7. Reorient Robot (reorient to 90/270 degrees if on platforms furthest from glpyh placement area)
     * Loop:
     *     8. Move across shelf slowly w/ Range Sensor
     *     9. Use counter var. to count how many times the Range Sensor reading suddenly dipped (shelf edges)
     *        and compare the var. to the key slot value
     * 10. Reorient robot after it finds the correct slot
     * 11. Park in parking zone by moving back (or forwards) to the 2nd slot (^similar loop)
     **/
     
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    Hardware robot=new Hardware();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Status", "Calibrating the Gyro..."");
        telemetry.update();
        robot.gyro.calibrate();
        telemetry.addData("Status", "Calibration Finished!");
        telemetry.update();
        

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            
            
            
            idle();
        }
    }

    //Reorients the Robot so it faces the shelves (or the nearest multiple of 90/ cardinal direction in relation to the initial header)
    public void reOrient(){
        float curHeading = (float)robot.gyro.getHeading();
        int targetHeading = 0; //Might change if block holder is on back of robot
        
        //Turns robot towards Target Heading
        if(curHeading!=targetHeading){
            if(curHeading > 180)){
                    robot.rightMotorB.setPower(.5); //test and set to power that'll ensure greatest accuracy:speed ratio
                    robot.rightMotorF.setPower(.5);
                    robot.leftMotorF.setPower(-.5);
                    robot.leftMotorB.setPower(-.5);
            }
            else
                    robot.rightMotorB.setPower(-.5); //test and set to power that'll ensure greatest accuracy:speed ratio
                    robot.rightMotorF.setPower(-.5);
                    robot.leftMotorF.setPower(.5);
                    robot.leftMotorB.setPower(.5);
            }
        }
        //when the loop breaks, the robot is at the targetHeading
        while (robot.gyro.getHeading() != targetHeading)
        {
            telemetry.addData("Status", "Reorienting... Please Wait...");
            telemetry.update();
        }
        robot.rightMotorB.setPower(0);   //get on the group chat :P (under the collaborate tab (top right))
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