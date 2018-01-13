package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
    public DcMotor raiseMotor = null;
    public DcMotor relicMotor = null;

    public Servo leftGrabServo = null; //For arm
    public Servo rightGrabServo = null;
    //public Servo railServo = null;
    //public Servo handSpinServo = null;
    public Servo jDropServo = null;
    public Servo jSlapServo = null;
    public Servp relicServo = null;

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
        
        //color = hwMap.get(ColorSensor.class, "color_sensor");
        //range = hwMap.get(ModernRoboticsI2cRangeSensor.class, "range_sensor");
        //gyro = hwMap.get(ModernRoboticsI2cGyro.class, "gyro");
        
        leftGrabServo = hwMap.servo.get("L Grab");
        rightGrabServo = hwMap.servo.get("R Grab");
        //railServo = hwMap.servo.get("Rail servo");
        jDropServo = hwMap.servo.get("Jewel drop");
        jSlapServo = hwMap.servo.get("Jewel slap");
        relicServo = hwMap.servo.get("Relic");

        leftMotorF = hwMap.dcMotor.get("Left motor");
        rightMotorF = hwMap.dcMotor.get("Right motor");
        leftMotorB = hwMap.dcMotor.get("Left motor 2");
        rightMotorB = hwMap.dcMotor.get("Right motor 2");
        raiseMotor = hwMap.dcMotor.get("Raise motor");
        relicMotor = hwMap.dcMotor.get("Relic motor");

        leftMotorF.setDirection(DcMotor.Direction.REVERSE);
        rightMotorF.setDirection(DcMotor.Direction.FORWARD);
        leftMotorB.setDirection(DcMotor.Direction.REVERSE);
        rightMotorB.setDirection(DcMotor.Direction.FORWARD);
        raiseMotor.setDirection(DcMotor.Direction.REVERSE);
        relicMotor.setDirection(DcMotor.Direction.REVERSE);
        
        leftGrabServo.setPosition(.08);
        rightGrabServo.setPosition(1);
        jDropServo.setPosition(.7103);
        jSlapServo.setPosition(.2671);
        relicServo.setDirection(.5);
        leftMotorF.setPower(0);
        rightMotorF.setPower(0);
        leftMotorB.setPower(0);
        rightMotorB.setPower(0);
        raiseMotor.setPower(0);
        relicMotor.setPower(0);

        leftMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        raiseMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        relicMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //armExtendMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //armLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    

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
