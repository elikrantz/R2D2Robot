package org.firstinspires.ftc.teamcode;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Device;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Random;

@TeleOp
public class R2D2drive extends LinearOpMode {
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private IMU imu;
    private Gamepad currentGamepad1;
    private Gamepad previousGamepad1;
    private AndroidSoundPool androidSoundPool;
    private ElapsedTime timeBetweenSound;

    private double speed1 = 0.5;
    private double soundTime = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        Initialization();

        if (isStopRequested()) return;

        /** when code starts these 4 lines assign an empty virtual gamepad (otherwise know as a controller) to each **/
        currentGamepad1 = new Gamepad();
        previousGamepad1 = new Gamepad();
        timeBetweenSound.reset();
        while (opModeIsActive()) {
            //currentIMUAngle = imu.getRobotOrientationAsQuaternion();
            previousGamepad1.copy(currentGamepad1); /** copies the previous loop's gamepad1 state **/
            currentGamepad1.copy(gamepad1); /** copies the current gamepad1 state **/
            robotOriented();
            //fieldOriented();
            telemetry.update();
            /*if (timeBetweenSound.seconds() >= soundTime) {
                soundTime = randomNum();
                timeBetweenSound.reset();
                String sound = randomSound();
                //androidSoundPool.preloadSound(sound);
                androidSoundPool.play(sound);
                timeBetweenSound.reset();
            }*/
        }
        androidSoundPool.close();
    }

    private void Initialization() {
        // Declare our motors
        // Make sure your ID's match your configuration
        frontLeft = hardwareMap.dcMotor.get("frontLeft"); /** Port: ControlHub MotorPort 1 **/
        backLeft = hardwareMap.dcMotor.get("backLeft"); /** Port: ControlHub MotorPort 0 **/
        frontRight = hardwareMap.dcMotor.get("frontRight"); /** Port: ExpansionHub MotorPort 3 **/
        backRight = hardwareMap.dcMotor.get("backRight"); /** Port: ExpansionHub MotorPort 2 **/

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        androidSoundPool = new AndroidSoundPool();
        androidSoundPool.initialize(SoundPlayer.getInstance());
        timeBetweenSound = new ElapsedTime();

        // Retrieve the IMU from the hardware map
        imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        ));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        imu.resetYaw();
        waitForStart();
    }

    private void fieldOriented() {
        double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = -gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;
        // This button choice was made so that it is hard to hit on accident,
        // it can be freely changed based on preference.
        // The equivalent button is start on Xbox-style controllers.
        /*if (gamepad1.options) {
            imu.resetYaw();
        }*/

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.2;  // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1.0);
        double frontLeftPower = speed1 * (rotY + rotX + rx) / denominator;
        double backLeftPower = speed1 * (rotY - rotX + rx) / denominator;
        double frontRightPower = speed1 * (rotY - rotX - rx) / denominator;
        double backRightPower = speed1 * (rotY + rotX - rx) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }
    private void robotOriented() {
        double yRobot = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double xRobot = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rxRobot = gamepad1.right_stick_x;

        if (Math.abs(gamepad1.left_stick_x) <= 0.05 && Math.abs(gamepad1.left_stick_y) <= 0.05) {
            yRobot = 0;
            xRobot = 0;
        }

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(yRobot) + Math.abs(xRobot) + Math.abs(rxRobot), 1);
        double frontLeftPower = speed1 * (yRobot + xRobot + rxRobot) / denominator;
        double backLeftPower = speed1 * (yRobot - xRobot + rxRobot) / denominator;
        double frontRightPower = speed1 * (yRobot - xRobot - rxRobot) / denominator;
        double backRightPower = speed1 * (yRobot + xRobot - rxRobot) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }

    public String randomSound() {
        String[] audioFiles = new String[] {"Voicy_R2-D2 - 10.mp3","Voicy_R2-D2 - 11.mp3","Voicy_R2-D2 - 12.mp3"};
        int randomInt = new Random().nextInt(3);
        return audioFiles[randomInt];
    }
    private int randomNum() {
        return new Random().nextInt(20) + 40;
    }
}
