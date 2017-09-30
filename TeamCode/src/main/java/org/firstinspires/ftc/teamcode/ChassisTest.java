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


//@TeleOp(name="Chassis Test", group="Linear Opmode")  // @Autonomous(...) is the other common choice
public class ChassisTest extends LinearOpMode {

    //Shooter shooter     = new Shooter();

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    //Hardware robot=new Hardware();
    @Override
    public void runOpMode() throws InterruptedException {
        //robot.init(hardwareMap);//, getClass().getSimpleName());
        //shooter.init(robot, gamepad2);
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        boolean pressed = false;
        boolean reverse = false;
        //float servPosition = 0;
        int MODIFIER = 10;
        //int sp = 1;
        float MODIFY;
        //boolean yPressed = false;
        boolean rbPressed = false, lbPressed = false;
        //boolean liftSOpen = false;


        while (opModeIsActive()) {

            //shooter.step();

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

            //panda panda panda panda panda panda
           float lsX = 0, lsY = 0;
           if (Math.abs(gamepad1.left_stick_x) >= 0.3 || Math.abs(gamepad1.left_stick_y) >= 0.2) {
               lsX = gamepad1.left_stick_x * MODIFY;
               lsY = gamepad1.left_stick_y * MODIFY * (reverse ? -1 : 1);
           }
            float mod = 0.5f;

            /*if (Math.abs(gamepad2.left_stick_y) >= 0.2) {
                servPosition += gamepad2.left_stick_y*0.02;
                if (servPosition > 1) { servPosition = 1; }
                if (servPosition < 0) { servPosition = 0; }

            }
            */

            //robot.bServo.setPosition(servPosition);//robot.bServo.setPosition(Math.abs(gamepad2.left_stick_y));


            /*robot.leftMotorF.setPower(lsY+lsX); //-
           robot.leftMotorB.setPower(lsY+lsX); //-
           robot.rightMotorF.setPower(lsY-lsX); //+
           robot.rightMotorB.setPower(lsY-lsX); //+
           */

            /*double pos = 0.68;
            if(gamepad2.a){
                pos = robot.shootServo.getPosition();
                if(pos+.01<=0.68)
                    robot.shootServo.setPosition(pos + .01);
            }
                if(gamepad2.b){
                pos = robot.shootServo.getPosition();
                if(pos-.01>=0.23)
                    robot.shootServo.setPosition(pos - .01);
            }*/

            /*if(Math.abs(gamepad2.left_stick_y) > 0.1) {
                robot.liftMotor.setPower(gamepad2.left_stick_y);
            }
            else
                robot.liftMotor.setPower(0);*/

            /*if(gamepad2.y && !yPressed)
            {
                yPressed = true;
                robot.sweepMotor.setPower(Math.abs(robot.sweepMotor.getPower()) == 1 ? 0 : sp);
            }
            else if(!gamepad2.y)
            {
                yPressed = false;
            }
            */
            if(gamepad2.left_bumper && !lbPressed)
            {
                lbPressed = true;
                //robot.gateServo.setPosition(robot.gateServo.getPosition() == 0.97 ? 0.70 : 0.97);
            }
            else if(!gamepad2.left_bumper)
            {
                lbPressed = false;
            }

            if(gamepad1.right_bumper && !rbPressed)
            {
                rbPressed = true;
                reverse = !reverse;

            }
            else if(!gamepad1.right_bumper)
            {
                rbPressed = false;
            }

            //telemetry.addData("Status", "Servo Pos: " + robot.shootServo.getPosition());
            ///telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("Status", "Left motors: " + robot.leftMotorF.getPower());
            //telemetry.addData("Status", "Right motors: " + robot.rightMotorF.getPower());
           telemetry.addData("Status", "Gamepad Y position: " + gamepad1.left_stick_y);
           telemetry.addData("Status", "Gamepad X position: " + gamepad1.left_stick_x);
           //telemetry.addData("Status", "Movement Modifier: " + MODIFY);


            telemetry.update();




            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
        //shooter.setInverse(false);
    }
    /*
    Left Motor Controller ID: AL00VWI4
    Right Motor Controller ID: AH03EUNQ
    Shoot Motor ID: AL00VVW7
    Servo ID: AIO2QSN8
    */
}
/*
kys    kys  kys     kys   kyskyskyskys
kys   kys    kys   kys    kys
kys  kys      kys kys     kys
kyskys          kys       kyskyskyskys
kys  kys        kys                kys
kys   kys       kys                kys
kys    kys      kys       kyskyskyskys
*/