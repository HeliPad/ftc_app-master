package org.firstinspires.ftc.teamcode;

/**
 * Created by TheBen on 11/28/2017.
 */

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

@TeleOp(name="Motor test", group="Linear Opmode")
public class MotorTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    Hardware robot=new Hardware();
    boolean pressedX = false;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        telemetry.addData("Status", "Testing right front");
        telemetry.update();

        robot.rightMotorF.setPower(1);
        waitForPress();

        telemetry.addData("Status", "Testing right back");
        telemetry.update();

        robot.rightMotorF.setPower(0);
        robot.rightMotorB.setPower(1);
        waitForPress();

        telemetry.addData("Status", "Testing left front");
        telemetry.update();

        robot.rightMotorB.setPower(0);
        robot.leftMotorF.setPower(1);
        waitForPress();

        telemetry.addData("Status", "Testing left back");
        telemetry.update();

        robot.leftMotorF.setPower(0);
        robot.leftMotorB.setPower(1);
        waitForPress();

        robot.leftMotorB.setPower(0);
    }

    public void waitForPress() {
        while (opModeIsActive()) {
            if (!pressedX && gamepad1.x) {
                pressedX = true;
                break;
            }
            else if (!gamepad1.x)
                pressedX = false;
        }
    }

}
