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


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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


//fall back Autonomous Program


@Autonomous(name="Half Autonomous", group="Automus")  // @Autonomous(...) is the other common choice
public class HalfAutonomous extends LinearOpMode {


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private Hardware robot= new Hardware();



//shoots into vortex, then goes into center area
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.init(hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        robot.leftMotorF.setPower(.2);
        robot.leftMotorB.setPower(.2);
        robot.rightMotorF.setPower(.2);
        robot.rightMotorB.setPower(.2);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive() && runtime.seconds() <= 0.7) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
        robot.leftMotorF.setPower(0);
        robot.rightMotorF.setPower(0);
        robot.leftMotorB.setPower(0);
        robot.rightMotorB.setPower(0);
        /*
        //robot.shootMotor.setPower(-1);
        //robot.sweepCMotor.setPower(1);
        while(opModeIsActive() && runtime.seconds()<=9.9){
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
        //robot.shootServo.setPosition(0.23);
        while(opModeIsActive() && runtime.seconds()<=12.9){
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
        */
        //robot.shootServo.setPosition(.68);
        //robot.shootMotor.setPower(0);
    }
}


