/*
Copyright (c) 2016 Robert Atkinson


All rights reserved.


Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:


Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.


Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.


Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.


NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */


@TeleOp(name="Linear OpMode", group="Linear Opmode")  // @Autonomous(...) is the other common choice


public class OpMode_Linear extends LinearOpMode {


    /* Declare OpMode members. */
    Hardware robot  = new Hardware();
    //Shooter shooter     = new Shooter();




    private ElapsedTime runtime = new ElapsedTime();
    // DcMotor leftMotorF = null;
    // DcMotor rightMotorF = null;


    @Override
    public void runOpMode() throws InterruptedException {
        //robot.init(hardwareMap, getClass().getSimpleName());
        //shooter.init();
        telemetry.addData("Status", "Initialized");
        telemetry.update();


       /* eg: Initialize the hardware variables. Note that the strings used here as parameters
        * to 'get' must correspond to the names assigned during the robot configuration
        * step (using the FTC Robot Controller app on the phone).
        */
        // leftMotorF  = hardwareMap.dcMotor.get("left motor");
        // rightMotorF = hardwareMap.dcMotor.get("right motor");


        // eg: Set the drive motor directions:
        // "Reverse" the motor that runs backwards when connected directly to the battery
        // leftMotorF.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        // rightMotorF.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
       /*while (opModeIsActive()) {
           telemetry.addData("Status", "Run Time: " + runtime.toString());
           telemetry.update();


           robot.leftMotorF.setPower(-gamepad1.left_stick_x - gamepad1.left_stick_y);
           robot.rightMotorF.setPower(-gamepad1.left_stick_x - gamepad1.left_stick_y);
           // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
           // leftMotorF.setPower(-gamepad1.left_stick_y);
           // rightMotorF.setPower(-gamepad1.right_stick_y);


           idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
       }*/


        boolean pressed = false;
        int MODIFIER = 10;
        float MODIFY;


        while (opModeIsActive()) {
           /*if(gamepad1.left_stick_x>0.2 || gamepad1.left_stick_x<-0.2) {
               robot.rightMotorF.setPower(gamepad1.left_stick_x);
               robot.leftMotorF.setPower(-gamepad1.left_stick_x);
           }
           // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards
           else if(gamepad1.left_stick_y>0.2 || gamepad1.left_stick_y<-0.2){
               robot.leftMotorF.setPower(-gamepad1.left_stick_y);
               robot.rightMotorF.setPower(-gamepad1.left_stick_y);
           }*/


            //float lsX = Math.abs(gamepad1.left_stick_x) >= 0.2 ? gamepad1.left_stick_x : 0;
            //float lsY = Math.abs(gamepad1.left_stick_y) >= 0.2 ? gamepad1.left_stick_y : 0;


            if (gamepad1.dpad_right && !pressed) {
                pressed = true;
                if (MODIFIER < 10) {
                    MODIFIER += 2;
                }
            }
            else if (gamepad1.dpad_left && !pressed) {
                pressed = true;
                if (MODIFIER > 2) {
                    MODIFIER -=2;
                }
            }
            else if (!gamepad1.dpad_right && !gamepad1.dpad_left)
                pressed = false;
            MODIFY= MODIFIER*0.1f;


            float lsX = 0, lsY = 0;
            if (Math.abs(gamepad1.left_stick_x) >= 0.1 || Math.abs(gamepad1.left_stick_y) >= 0.1) {
                lsX = gamepad1.left_stick_x * 0.2f * MODIFY;
                lsY = gamepad1.left_stick_y * MODIFY;
            }


            robot.leftMotorF.setPower(lsY-lsX);
            robot.rightMotorF.setPower(lsY+lsX);


            telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("Status", "Gamepad Y position: " + gamepad1.left_stick_y);
            //telemetry.addData("Status", "Gamepad X position: " + gamepad1.left_stick_x);
            telemetry.addData("Status", "Movement Modifier: " + MODIFY);
            telemetry.addData("Status", "Left motor power: " + (lsY-lsX));
            telemetry.addData("Status", "Right motor power: " + (lsY+lsX));


            telemetry.update();


            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }
}
