package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class Hardware
{
    /* Public OpMode members. */
    public DcMotor leftMotorF = null;
    public DcMotor rightMotorF = null;
    public DcMotor leftMotorB = null;
    public DcMotor rightMotorB = null;
    public ColorSensor color = null;
    public ModernRoboticsI2cRangeSensor range = null;
    public ModernRoboticsI2cGyro gyro = null;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public Hardware(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {//, String className) {

        // Save reference to Hardware map
        hwMap = ahwMap;
        /*
        //calls method(s) that initialize only the motors being used in order to avoid errors
        if (className.equals("ShooterTestOpMode_Linear"))
            shootInit();
        else {
            shootInit();
            mobInit();
            transInit();
        }
        */
        color = hwMap.get(ColorSensor.class, "color_sensor");
        range = hwMap.get(ModernRoboticsI2cRangeSensor.class, "range_sensor");
        gyro = hwMap.get(ModernRoboticsI2cGyro.class, "gyro");
        mobInit();
    }
    //ShootInit Initializes the Shooting mechanism.
    /*public void shootInit(){
        shootMotor = hwMap.dcMotor.get("Shoot motor");
        shootMotor.setDirection(DcMotor.Direction.FORWARD);
        shootMotor.setPower(0);
        shootMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shootServo=hwMap.servo.get("Shoot servo");
        shootServo.setDirection(Servo.Direction.FORWARD);
        shootServo.setPosition(.68);

        gateServo=hwMap.servo.get("Gate servo");
        gateServo.setDirection(Servo.Direction.FORWARD);
        gateServo.setPosition(.97);

    }*/

    //Initializes the motors specifically for transporting the ball up to the shooting mechanism
    /*public void transInit(){
        sweepMotor  = hwMap.dcMotor.get("Sweep motor");
        sweepMotor.setDirection(DcMotor.Direction.FORWARD);
        sweepMotor.setPower(0);
        sweepMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }*/

    //MobInit Initializes the Motors specifically for testing the mobility of the robot
    public void mobInit(){
        leftMotorF = hwMap.dcMotor.get("Left motor");
        rightMotorF = hwMap.dcMotor.get("Right motor");
        leftMotorB = hwMap.dcMotor.get("Left motor 2");
        rightMotorB = hwMap.dcMotor.get("Right motor 2");

        leftMotorF.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightMotorF.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        leftMotorB.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightMotorB.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

        leftMotorF.setPower(0);
        rightMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorB.setPower(0);

        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    /*public void liftInit(){
        liftMotor  = hwMap.dcMotor.get("Lift motor");
        liftMotor.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        liftMotor.setPower(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftServo=hwMap.servo.get("Lift servo 1");
        liftServo.setDirection(Servo.Direction.FORWARD);
        liftServo.setPosition(.5);
    }*/

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     * @throws InterruptedException
     */
    public void waitForTick(long periodMs) throws InterruptedException {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0)
            Thread.sleep(remaining);

        // Reset the cycle clock for the next pass.
        period.reset();
    }
}
