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

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

/**
 * This OpMode illustrates the basics of using the Vuforia localizer to determine
 * positioning and orientation of robot on the FTC field.
 * The code is structured as a LinearOpMode
 *
 * Vuforia uses the phone's camera to inspect it's surroundings, and attempt to locate target images.
 *
 * When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This sample code than combines that information with a
 * knowledge of where the target images are on the field, to determine the location of the camera.
 *
 * This example assumes a "diamond" field configuration where the red and blue alliance stations
 * are adjacent on the corner of the field furthest from the audience.
 * From the Audience perspective, the Red driver station is on the right.
 * The two vision target are located on the two walls closest to the audience, facing in.
 * The Stones are on the RED side of the field, and the Chips are on the Blue side.
 *
 * A final calculation then uses the location of the camera on the robot to determine the
 * robot's location and orientation on the field.
 *
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  ftc_app/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */

@Autonomous(name="Vuforia Nav", group ="Concept")
public class VuforiaNav extends LinearOpMode {

    public static final String TAG = "Target Found!";

    OpenGLMatrix lastLocation = null;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    float robotX;
    float robotY;
    float robotOrient;
    List<Float> points= new ArrayList<Float>();
    boolean isBlueTeam = false;
    float[] coords;
    /**
     * We use units of mm here because that's the recommended units of measurement for the
     * size values specified in the XML for the ImageTarget trackables in data sets. E.g.:
     *      <ImageTarget name="stones" size="247 173"/>
     * You don't *have to* use mm here, but the units here and the units used in the XML
     * target configuration files *must* correspond for the math to work out correctly.
     */
    float mmPerInch        = 25.4f;
    float mmBotWidth       = 18 * mmPerInch;            // ... or whatever is right for your robot
    float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels
    private Hardware robot= new Hardware();
    private Shooter shooter= new Shooter();
    @Override public void runOpMode() throws InterruptedException {
        //robot.init(hardwareMap, getClass().getSimpleName());
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        /*initializes points that robot will follow/navigate to (in mm) (insert points here)
        Be sure to consult https://drive.google.com/drive/folders/0B9c624m6itsUMGF5cnJrQVB2MWs in conjunction with
        https://firstinspiresst01.blob.core.windows.net/ftc/game-manual-part-2.pdf (page 9) for coordinate plane and axes
        */

        if(isBlueTeam) {
            //x
            points.add(-mmFTCFieldWidth / 2);
            //y
            points.add(mmFTCFieldWidth / 2);
        }
        else{
            points.add(mmFTCFieldWidth/ 12 * -1);
            points.add(mmFTCFieldWidth/ 12 * -1);
        }
        /**
         * Start up Vuforia, telling it the id of the view that we wish to use as the parent for
         * the camera monitor feedback; if no camera monitor feedback is desired, use the parameterless
         * constructor instead. We also indicate which camera on the RC that we wish to use. For illustration
         * purposes here, we choose the back camera; for a competition robot, the front camera might
         * prove to be more convenient.
         *
         * Note that in addition to indicating which camera is in use, we also need to tell the system
         * the location of the phone on the robot; see phoneLocationOnRobot below.
         *
         * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
         * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
         * Vuforia will not load without a valid license being provided. Vuforia 'Development' license
         * keys, which is what is needed here, can be obtained free of charge from the Vuforia developer
         * web site at https://developer.vuforia.com/license-manager.
         *
         * Valid Vuforia license keys are always 380 characters long, and look as if they contain mostly
         * random data. As an example, here is a example of a fragment of a valid key:
         *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         * Once you've obtained a license key, copy the string form of the key from the Vuforia web site
         * and paste it in to your code as the value of the 'vuforiaLicenseKey' field of the
         * {@link Parameters} instance with which you initialize Vuforia.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AQciC17/////AAAAGcHm/ae3S08LoppTUUYYhUt7QoRjsJ4DoYOBzI9Dm8tvE3Q/J" +
                "tmd61aFoXpPo27FDqgLMGRfL50nNxwiyLGgilckQSUmvdaOM5u+66J6rNQk3KTkQb+BsajnaJ8ekm525CPOEoTGK0" +
                "QLsuHPISeLIUUPdegnqhtyEZCQZE5bcvDQfv6aT0BvDZGpm09qhQmFWpmZrU1nNaLwCOELCD8g/Q9s2TfBi2BZtkC 5S" +
                "/CeKmzfrzed7RWQo0showZqx4bEQZLMgYUedAxvjaF8mknAuP1oMGb0udO/b0w6oLUHiGzXTaaf+q7zcTh8SXPLsIpPu4a" +
                "v6gGiENWcUz7hczRQEMi3ZqJIyjbLCyV7psrGovSp";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data sets that for the trackable objects we wish to track. These particular data
         * sets are stored in the 'assets' part of our application (you'll see them in the Android
         * Studio 'Project' view over there on the left of the screen). You can make your own datasets
         * with the Vuforia Target Manager: https://developer.vuforia.com/target-manager. PDFs for the
         * example "StonesAndChips", datasets can be found in in this project in the
         * documentation directory.
         */
        VuforiaTrackables targets = this.vuforia.loadTrackablesFromAsset("FTC_2016-17");
        VuforiaTrackable Wheels = targets.get(0);
        Wheels.setName("Wheels Target");  // Wheels

        VuforiaTrackable Legos  = targets.get(1);
        Legos.setName("Legos Target");  // Legos!!!

        VuforiaTrackable Tools = targets.get(2);
        Tools.setName("Tools Target");  // Tools

        VuforiaTrackable Gears  = targets.get(3);
        Gears.setName("Gears Target");  //Gears!!!

        /** For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targets);


        /**
         * In order for localization to work, we need to tell the system where each target we
         * wish to use for navigation resides on the field, and we need to specify where on the robot
         * the phone resides. These specifications are in the form of <em>transformation matrices.</em>
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the {@link OpenGLMatrix} class.
         *
         * For the most part, you don't need to understand the details of the math of how transformation
         * matrices work inside (as fascinating as that is, truly). Just remember these key points:
         * <ol>
         *
         *     <li>You can put two transformations together to produce a third that combines the effect of
         *     both of them. If, for example, you have a rotation transform R and a translation transform T,
         *     then the combined transformation matrix RT which does the rotation first and then the translation
         *     is given by {@code RT = T.multiplied(R)}. That is, the transforms are multiplied in the
         *     <em>reverse</em> of the chronological order in which they applied.</li>
         *
         *     <li>A common way to create useful transforms is to use methods in the {@link OpenGLMatrix}
         *     class and the Orientation class. See, for example, {@link OpenGLMatrix#translation(float,
         *     float, float)}, {@link OpenGLMatrix#rotation(AngleUnit, float, float, float, float)}, and
         *     {@link Orientation#getRotationMatrix(AxesReference, AxesOrder, AngleUnit, float, float, float)}.
         *     Related methods in {@link OpenGLMatrix}, such as {@link OpenGLMatrix#rotated(AngleUnit,
         *     float, float, float, float)}, are syntactic shorthands for creating a new transform and
         *     then immediately multiplying the receiver by it, which can be convenient at times.</li>
         *
         *     <li>If you want to break open the black box of a transformation matrix to understand
         *     what it's doing inside, use {@link MatrixF#getTranslation()} to fetch how much the
         *     transform will move you in x, y, and z, and use {@link Orientation#getOrientation(MatrixF,
         *     AxesReference, AxesOrder, AngleUnit)} to determine the rotational motion that the transform
         *     will impart. See {@link #format(OpenGLMatrix)} below for an example.</li>
         *
         * </ol>
         *
         * This example places the "stones" image on the perimeter wall to the Left
         *  of the Red Driver station wall.  Similar to the Red Beacon Location on the Res-Q
         *
         * This example places the "chips" image on the perimeter wall to the Right
         *  of the Blue Driver station.  Similar to the Blue Beacon Location on the Res-Q
         *
         * See the doc folder of this project for a description of the field Axis conventions.
         *
         * Initially the target is conceptually lying at the origin of the field's coordinate system
         * (the center of the field), facing up.
         *
         * In this configuration, the target's coordinate system aligns with that of the field.
         *
         * In a real situation we'd also account for the vertical (Z) offset of the target,
         * but for simplicity, we ignore that here; for a real robot, you'll want to fix that.
         *
         * To place the Stones Target on the Red Audience wall:
         * - First we rotate it 90 around the field's X axis to flip it upright
         * - Then we rotate it  90 around the field's Z access to face it away from the audience.
         * - Finally, we translate it back along the X axis towards the red audience wall.
         */
        OpenGLMatrix toolsTargetLocationOnField = OpenGLMatrix
               /* Then we translate the target off to the RED WALL. Our translation here
               is a negative translation in X.*/
                .translation(-mmFTCFieldWidth/2, mmFTCFieldWidth/4, 0)
                .multiplied(Orientation.getRotationMatrix(
                       /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        Tools.setLocation(toolsTargetLocationOnField);
        telemetry.addData(TAG, "Tools Target=%s", format(toolsTargetLocationOnField));

      /*
       * To place the Stones Target on the Blue Audience wall:
       * - First we rotate it 90 around the field's X axis to flip it upright
       * - Finally, we translate it along the Y axis towards the blue audience wall.
       */
        OpenGLMatrix wheelsTargetLocationOnField = OpenGLMatrix
               /* Then we translate the target off to the Blue Audience wall.
               Our translation here is a positive translation in Y.*/
                .translation(mmFTCFieldWidth/12, mmFTCFieldWidth/2, 0)
                .multiplied(Orientation.getRotationMatrix(
                       /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        Wheels.setLocation(wheelsTargetLocationOnField);
        telemetry.addData(TAG, "Wheels Target=%s", format(wheelsTargetLocationOnField));

        OpenGLMatrix legosTargetLocationOnField = OpenGLMatrix
                //Red audience wall (with Wheels)
                .translation(-mmFTCFieldWidth/2, -mmFTCFieldWidth/12, 0)
                .multiplied(Orientation.getRotationMatrix(
                       /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        Legos.setLocation(legosTargetLocationOnField);
        telemetry.addData(TAG, "Legos Target=%s", format(legosTargetLocationOnField));

      /*
       * To place the Stones Target on the Blue Audience wall:
       * - First we rotate it 90 around the field's X axis to flip it upright
       * - Finally, we translate it along the ***Y axis towards the blue audience wall.****
       */
        OpenGLMatrix gearsTargetLocationOnField = OpenGLMatrix
                //Blue audience wall (with legos) ---Change if needed
                .translation(-mmFTCFieldWidth/4, mmFTCFieldWidth/2, 0)
                .multiplied(Orientation.getRotationMatrix(
                       /* First, in the fixed (field) coordinate system, we rotate 90deg in X */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        Gears.setLocation(gearsTargetLocationOnField);
        telemetry.addData(TAG, "Gears Target=%s", format(gearsTargetLocationOnField));
        /**
         * Create a transformation matrix describing where the phone is on the robot. Here, we
         * put the phone on the right hand side of the robot with the screen facing in (see our
         * choice of BACK camera above) and in landscape mode. Starting from alignment between the
         * robot's and phone's axes, this is a rotation of -90deg along the Y axis.
         *
         * When determining whether a rotation is positive or negative, consider yourself as looking
         * down the (positive) axis of rotation from the positive towards the origin. Positive rotations
         * are then CCW, and negative rotations CW. An example: consider looking down the positive Z
         * axis towards the origin. A positive rotation about Z (ie: a rotation parallel to the the X-Y
         * plane) is then CCW, as one would normally expect from the usual classic 2D geometry.
         */
        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(mmBotWidth/2 + 1,0,0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));
        telemetry.addData(TAG, "phone=%s", format(phoneLocationOnRobot));

        /**
         * Let the trackable listeners we care about know where the phone is. We know that each
         * listener is a {@link VuforiaTrackableDefaultListener} and can so safely cast because
         * we have not ourselves installed a listener of a different type.
         */
        ((VuforiaTrackableDefaultListener)Wheels.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener)Legos.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener)Tools.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener)Gears.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);

        /**
         * A brief tutorial: here's how all the math is going to work:
         *
         * C = phoneLocationOnRobot  maps   phone coords -> robot coords
         * P = tracker.getPose()     maps   image target coords -> phone coords
         * L = redTargetLocationOnField maps   image target coords -> field coords
         *
         * So
         *
         * C.inverted()              maps   robot coords -> phone coords
         * P.inverted()              maps   phone coords -> imageTarget coords
         *
         * Putting that all together,
         *
         * L x P.inverted() x C.inverted() maps robot coords to field coords.
         *
         * @see VuforiaTrackableDefaultListener#getRobotLocation()
         */

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        /** Start tracking the data sets we care about. */
        targets.activate();
        int quad;
        for(int i=0;i<points.size() && opModeIsActive();i+=2) {
            telemetry.addData(">", "Finding the robot's position...");
            telemetry.update();
            updatePos(allTrackables);
            telemetry.addData(">", "Found Position, Calculating angle of approach...");
            telemetry.update();
            float angle = calcAngle(i);
            float rotR= Math.abs((360-angle) - (360-robotOrient));
            float rotL= Math.abs((360-angle) + (360 + robotOrient));
            //rotates to the right (decreases in angle)
            if (rotR <= rotL) {
                robot.leftMotorF.setPower(.1);
                robot.rightMotorB.setPower(-.1);
                robot.rightMotorF.setPower(-.1);
                robot.leftMotorB.setPower(.1);
                while (opModeIsActive() && robotOrient<=angle) {
                    updatePos(allTrackables);
                    idle();
                }
            //rotates to the left (increases in degrees)
            } else {
                robot.leftMotorF.setPower(-.1);
                robot.rightMotorB.setPower(.1);
                robot.rightMotorF.setPower(.1);
                robot.leftMotorB.setPower(-.1);

                while (opModeIsActive() && robotOrient>=angle) {
                    updatePos(allTrackables);
                    idle();
                }
            }
            //after robot turns to the point's angle w/ the robot as the origin, stops
            robot.leftMotorF.setPower(0);
            robot.rightMotorB.setPower(0);
            robot.rightMotorF.setPower(0);
            robot.leftMotorB.setPower(0);
            //to allow motors to fully stop, I added a sleep command
            sleep(420);
            robot.leftMotorB.setPower(.2);
            robot.rightMotorB.setPower(.2);
            robot.rightMotorF.setPower(.2);
            robot.leftMotorF.setPower(.2);
            quad=findQuad(angle);
            if(quad==1){
                while(opModeIsActive() && robotX<=points.get(i) && robotY<=points.get(i+1)){
                    updatePos(allTrackables);
                    idle();
                }
            }//dank memes
            //panda panda panda panda panda panda
            else if(quad==2){
                while(opModeIsActive() && robotX>=points.get(i) && robotY<=points.get(i+1)){
                    updatePos(allTrackables);
                    idle();
                }
            }
            else if(quad==3){
                while(opModeIsActive() && robotX>=points.get(i) && robotY>=points.get(i+1)){
                    updatePos(allTrackables);
                    idle();
                }
            }
            else{
                while(opModeIsActive() && robotX<=points.get(i) && robotY>=points.get(i+1)){
                    updatePos(allTrackables);
                    idle();
                }
            }
            robot.leftMotorF.setPower(0);
            robot.rightMotorB.setPower(0);
            robot.rightMotorF.setPower(0);
            robot.leftMotorB.setPower(0);
            sleep(420);
        }
    }

    /**
     * A simple utility that extracts positioning information from a transformation matrix
     * and formats it in a form palatable to a human being.
     */
    String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
    int findQuad(float angle){
        if(angle<=90)
            return 1;
        else if(angle>=270)
            return 4;
        else if(angle>=180)
            return 3;
        else
            return 2;
    }
    //calculates angle of the point
    float calcAngle(int pos){
        //yPosTrans is true when the vertical distance from the robot to the point is positive
        boolean yPosTrans=false;
        //xPosTrans is true when the horizontal distance from the robot to the point is positive
        boolean xPosTrans=false;
        if(robotY<=points.get(pos))
            yPosTrans=true;
        if(robotX<points.get(pos+1))
            xPosTrans=true;
        float xDist= points.get(pos)-robotX;
        float yDist= points.get(pos+1)-robotY;
        float refAngle= (float)(Math.toDegrees(Math.atan(xDist/yDist)));
        float angle=0f;
        if(yPosTrans)
            angle= 90f-refAngle;
        else
            angle=270f-refAngle;
        return angle;
    }
    void updatePos(List<VuforiaTrackable> allTrackables){
        /**
         * Provide feedback as to where the robot was last located (if we know).
         */
        for (VuforiaTrackable trackable : allTrackables) {
            /**
             * getUpdatedRobotLocation() will return null if no new information is available since
             * the last time that call was made, or if the trackable is not currently visible.
             * getRobotLocation() will return null if the trackable is not currently visible.
             */
            telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible() ? "Visible" : "Not Visible");    //

            OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
            }

        }
        if (lastLocation != null) {
            //  RobotLog.vv(TAG, "robot=%s", format(lastLocation));
            telemetry.addData("Pos", format(lastLocation));
        } else {
            telemetry.addData("Pos", "Unknown");
        }
        telemetry.update();
        coords= lastLocation.getTranslation().getData();
        robotX=coords[0];
        robotY=coords[1];
        robotOrient= Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
    }

}
