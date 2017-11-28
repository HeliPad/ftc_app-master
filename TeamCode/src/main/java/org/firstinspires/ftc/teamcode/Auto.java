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
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
//import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.teamcode.Tween;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
//import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

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

//@Autonomous(name="Auto", group="Automus")
public class Auto extends LinearOpMode {
    /**
     * Steps for Autonomous:
     * 1. Extend Bar
     * 2. Use Color Sensor to Detect Jewel Color
     * 3. Hit Opposing Alliance Jewel w/ Bar
     * 4. Detract Bar
     * 5. Read Image w/ Vuforia (Done!) 
     * 6. Go off platform towards shelves
     * 7. Reorient Robot (reorient to 90/270 degrees if on platforms furthest from glpyh placement area)
     * Loop:
     *     8. Move across shelf slowly w/ Range Sensor
     *     9. Use counter var. to count how many times the Range Sensor reading suddenly dipped
     *     (shelf edges - 3.93" inches (approx 9.98 cm.) deep) and compare the var. to the key slot value
     * 10. Reorient robot after it finds the correct slot
     * 11. Park in parking zone by moving back (or forwards) to the 2nd slot (^similar loop)
     **/

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Tween tween = new Tween(runtime);
    Hardware robot=new Hardware();
    private boolean isBlue=false;
    private boolean doTurn=false;
    public Auto(boolean isBlue, boolean doTurn) {
        this.isBlue=isBlue;
        this.doTurn=doTurn;
    }

    private int mod(int num, int div) {
        return num - (int)Math.floor((float)num/div)*div;
    }

    private int angleDifference(int a1, int a2) {
        return mod(a1 - a2 + 180, 360) - 180;
    }

    public void runOpMode() {
        robot.init(hardwareMap);

        VuforiaLocalizer vuforia;
        int cameraMonitorViewId = robot.hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", robot.hwMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AQciC17/////AAAAGcHm/ae3S08LoppTUUYYhUt7QoRjsJ4DoYOBzI9Dm8tvE3Q/J" +
                "tmd61aFoXpPo27FDqgLMGRfL50nNxwiyLGgilckQSUmvdaOM5u+66J6rNQk3KTkQb+BsajnaJ8ekm525CPOEoTGK0" +
                "QLsuHPISeLIUUPdegnqhtyEZCQZE5bcvDQfv6aT0BvDZGpm09qhQmFWpmZrU1nNaLwCOELCD8g/Q9s2TfBi2BZtkC" +
                "5S/CeKmzfrzed7RWQo0showZqx4bEQZLMgYUedAxvjaF8mknAuP1oMGb0udO/b0w6oLUHiGzXTaaf+q7zcTh8SXPLsIp" +
                "Pu4av6gGiENWcUz7hczRQEMi3ZqJIyjbLCyV7psrGovSp";

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);

        telemetry.addData("Status", "Initialized");

        telemetry.addData("Status", "Calibrating the Gyro...");
        telemetry.update();

        robot.gyro.calibrate();

        telemetry.addData("Status", "Calibration Finished!");
        telemetry.update();

        // For color sensor
        int red;
        int blue;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        relicTrackables.activate();

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

        while (opModeIsActive() && vuMark == RelicRecoveryVuMark.UNKNOWN) { // for finding starting image
            vuMark = RelicRecoveryVuMark.from(relicTemplate); // will be an Enum like RelicRecoveryVuMark.UNKNOWN or RelicRecoveryVuMark.CENTER
            idle();
        }
        telemetry.addData("Status", "Found image! Key is the " + vuMark + "!");
        telemetry.update();

        // Sets color sensor to active mode (for reading objects that aren't light sources) 
        robot.color.enableLed(true);

        // Extend Bar (might need encoders)
        while(opModeIsActive() /*&& motor distance < #*/){
            //turn on motor that extends bar
            idle();
        }
        // turn off motor that extends bar

        sleep(200); // Gives motor time to stop

        // Color Sensor stuff:
        // Get the red and blue values from RGB
        red=robot.color.red();
        blue=robot.color.blue();
        if(red>blue){
            // hit ball based on alliance
            if(isBlue){
                
            }
            else{
                
            }
        }
        else if(blue>red){
            // hit ball based on alliance
            if(isBlue){
                
            }
            else{
                
            }
        }
        // Retract bar (when motor has turned a certain distance, start driving off platform)
        while(opModeIsActive() /*&& motor distance < #*/){
            // turn on motor that controls bar
            idle();
        }
        // turn off motor
        // Turn robot if class that calls object is R2 or B2
        if(doTurn){
            reOrient(.5,0,0,0,0, false, (isBlue ? 270 : 90));
        }
        // drive off platform (Can use gyro's X or Y angular velocity to know how far to go) <-- when angular velocity == 0, stop motors
        if(doTurn){
            setMotorP(1, 1, 1, 1);
        }
        else if(!isBlue){
            setMotorP(.1, -.1, -.1, .1);
        }
        else{
            setMotorP(-.1, .1, .1, -.1);
        }
        
        // Tells robot when to stop
        AngularVelocity rates = robot.gyro.getAngularVelocity(AngleUnit.DEGREES);
        if(!doTurn){
            float dyAngle= rates.yRotationRate;
            while(opModeIsActive() && Math.abs(dyAngle) < 20.0){
                rates = robot.gyro.getAngularVelocity(AngleUnit.DEGREES);
                dyAngle = rates.yRotationRate;
                idle();
            }
            setMotorP(0, 0, 0, 0);
            
            // Moves robot so it's consistenly 15 cm. from wall
            if(robot.range.getDistance(DistanceUnit.CM)>15){
                setMotorP(.2, .2, .2, .2);
                while(robot.range.getDistance(DistanceUnit.CM)>15){
                    idle();
                }
            }
            else if(robot.range.getDistance(DistanceUnit.CM)<15){
                setMotorP(-.2, -.2, -.2, -.2);
                while(robot.range.getDistance(DistanceUnit.CM)<15){
                    idle();
                }
            }
        }
        else{
            setMotorP(.5, .5, .5, .5);
            while(robot.range.getDistance(DistanceUnit.CM) > 15){
                telemetry.addData("Status:" , "Moving towards shelves...");
                telemetry.update();
            }
            setMotorP(0, 0, 0, 0);
            telemetry.addData("Status:" , "Done!");
            telemetry.update();
            // Move robot to right (or left depending on alliance) to see if it's before the start of the shelves
            double startTime = runtime.seconds();
            if(!isBlue){
                setMotorP(-.5, .5, .5, -.5);
            }
            else{
                setMotorP(.5, -.5, -.5, .5);
            }
            double prevDist = robot.range.getDistance(DistanceUnit.CM);
            double curDist;
            // Checks to see if the robot passes a shelf, then adds 2 seconds longer to check 
            while(runtime.seconds() < startTime + 2){
                curDist = robot.range.getDistance(DistanceUnit.CM);
                // Adds two seconds if it finds a shelf 
                if(curDist<=prevDist - 7){
                    startTime=(int)runtime.seconds();
                }
                prevDist=curDist;
            }
            
        }
        setMotorP(0,0,0,0);
        
        // Reorients robot to face the wall
        reOrient(.5,0,0,0,0, false, (doTurn ? (isBlue ? 270 : 90) : 0));
        
        // Translate to Right or Left while doing range sensor stuff
        if(!isBlue){
            setMotorP(.1, -.1, -.1, .1);
        }
        else{
            setMotorP(-.1,.1,.1,-.1);
        }

        // Range sensor goes on the rightmost point of the robot
        double prevDistance = robot.range.getDistance(DistanceUnit.CM);
        double curDistance;
        int c=0; // # of times robot passes shelf edge
        int curHeading;
        while(opModeIsActive()){
            curDistance = robot.range.getDistance(DistanceUnit.CM);
            // Reorient code goes here:
            curHeading = robot.gyro.getHeading();

            if (Math.abs(angleDifference((doTurn ? (isBlue ? 270:90) : 0), curHeading)) > 1){
                reOrient(.05, .1, -.1, -.1, .1, true, (doTurn ? (isBlue ? 270:90) : 0));
            }

            if(curDistance<prevDistance - 7){
                c++;
            }

            if(c==(isBlue ? 2 : 1) && vuMark.toString().equals((isBlue ? "LEFT" : "RIGHT"))){
                setMotorP(0,0,0,0);

                // Plack blocku

                break;
            }
            
            else if(c==(isBlue ? 3 : 2) && vuMark.toString().equals("CENTER")){
                setMotorP(0,0,0,0);
                // Place blocku

                break;
            }
            else if(c==(isBlue ? 4 : 3) && vuMark.toString().equals((isBlue ? "RIGHT" : "LEFT"))){
                setMotorP(0,0,0,0);

                // Place blocku
                
                break;
            }
            prevDistance = curDistance;
            idle();
        }
        // Move to 2nd slot and park
        prevDistance = robot.range.getDistance(DistanceUnit.CM);
        c=0;
        double startTime = runtime.seconds();
        while(opModeIsActive() && !vuMark.toString().equals("CENTER")) {
            curDistance = robot.range.getDistance(DistanceUnit.CM);
            if(curDistance<=prevDistance - 7 && runtime.seconds()-startTime > 1){
                c++;
            }
            if(vuMark.toString().equals("LEFT")){
                setMotorP(-.1, .1, .1, -.1);
                if(c==1){
                    setMotorP(0, 0, 0, 0);
                    break;
                }
            }
            else if(vuMark.toString().equals("RIGHT")){
                setMotorP(.1, -.1, -.1, .1);
                if(c==1){
                    setMotorP(0, 0, 0, 0);
                    break;
                }
            }
            prevDistance = curDistance;
            idle();
        }
        telemetry.addData("Status", "Finished!");
        telemetry.update();
    }

    // Reorients the Robot so it faces the shelves (or the nearest multiple of 90/ cardinal direction in relation to the initial header)
    private void reOrient(double power, double rf, double rb, double lf, double lb, boolean step, int targetHeading){
        float curHeading = (float)robot.gyro.getHeading();

        // Turns robot towards Target Heading
        if(curHeading!=targetHeading){
            if(curHeading > 180){
                setMotorP(power + rf, power + rb, -power + lf, -power + lb); //test and set to power that'll ensure greatest accuracy:speed ratio

            }
            else{
                setMotorP(-power + rf, -power + rb, power + lf, power + lb); //test and set to power that'll ensure greatest accuracy:speed ratio

            }
        }
        if(step){
            if(robot.gyro.getHeading()!=targetHeading){
                return;
            }
        }
        else{
            // when the loop breaks, the robot is at the targetHeading
            while (robot.gyro.getHeading() != targetHeading)
            {
                telemetry.addData("Status", "Reorienting... Please Wait...");
                telemetry.update();
            }
        }
        setMotorP(rf, rb, lf, lb);
        telemetry.addData("Status","Done!");
        telemetry.update();
    }
    private void setMotorP(double rf, double rb, double lf, double lb ){
        robot.rightMotorF.setPower(rf);
        robot.rightMotorB.setPower(rb);
        robot.leftMotorF.setPower(lf);
        robot.leftMotorB.setPower(lb);
    }
}
// https://gist.github.com/jboulhous/6007980

/*
git add .
git commit -m "[Insert Commit Here]"

git push -u origin master

git pull

Alliance specific things: 
- Change direction when moving off BB <-- done
- account for range sensor position (range sensor side of robot reaches shelf first on blue alliance)
- reverse direction for turning to face shelves
*/