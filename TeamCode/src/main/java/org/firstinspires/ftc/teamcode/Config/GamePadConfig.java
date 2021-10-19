package org.firstinspires.ftc.teamcode.Config;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Robot;

/**
 * GamePadConfig has the Config for the gamepad, which is used in robot.java.
 *
 * <p>It also maps the gamepad in mapGamePadInputs(), this is also used by the robot subsystem. This improves Robot.java's code quality</p>
 * @see org.firstinspires.ftc.teamcode.Subsystems.Robot
 */
public class GamePadConfig {
    // Declare game pad objects
    public double leftStickX;
    public double leftStickY;
    public double rightStickX;
    public double rightStickY;
    public float triggerLeft;
    public float triggerRight;
    public boolean aButton = false;
    public boolean bButton = false;
    public boolean xButton = false;
    public boolean yButton = false;
    public boolean dPadUp = false;
    public boolean dPadDown = false;
    public boolean dPadLeft = false;
    public boolean dPadRight = false;
    public boolean bumperLeft = false;
    public boolean bumperRight = false;

    public double leftStickX2;
    public double leftStickY2;
    public double rightStickX2;
    public double rightStickY2;
    public float triggerLeft2;
    public float triggerRight2;
    public boolean aButton2 = false;
    public boolean bButton2 = false;
    public boolean xButton2 = false;
    public boolean yButton2 = false;
    public boolean dPadUp2 = false;
    public boolean dPadDown2 = false;
    public boolean dPadLeft2 = false;
    public boolean dPadRight2 = false;
    public boolean bumperLeft2 = false;
    public boolean bumperRight2 = false;

    public boolean isaButtonPressedPrev = false;
    public boolean isbButtonPressedPrev = false;
    public boolean isxButtonPressedPrev = false;
    public boolean isyButtonPressedPrev = false;
    public boolean isdPadUpPressedPrev = false;
    public boolean isdPadDownPressedPrev = false;
    public boolean isdPadLeftPressedPrev = false;
    public boolean isdPadRightPressedPrev = false;
    public boolean islBumperPressedPrev = false;
    public boolean isrBumperPressedPrev = false;
    public boolean isaButton2PressedPrev = false;
    public boolean isbButton2PressedPrev = false;
    public boolean isxButton2PressedPrev = false;
    public boolean isyButton2PressedPrev = false;
    public boolean isdPadUp2PressedPrev = false;
    public boolean isdPadDown2PressedPrev = false;
    public boolean isdPadLeft2PressedPrev = false;
    public boolean isdPadRight2PressedPrev = false;
    public boolean islBumper2PressedPrev = false;
    public boolean isrBumper2PressedPrev = false;

    /**
     * This maps the game pad inputs to variables, simplifying Robot.java's code and improving code quality
     * @param robot the robot subsystem
     * @see org.firstinspires.ftc.teamcode.Subsystems.Robot#getGamePadInputs()
     */
    public void mapGamePadInputs(Robot robot){
        LinearOpMode opMode = robot.getOpMode();
        isaButtonPressedPrev = aButton;
        isbButtonPressedPrev = bButton;
        isxButtonPressedPrev = xButton;
        isyButtonPressedPrev = yButton;
        isdPadUpPressedPrev = dPadUp;
        isdPadDownPressedPrev = dPadDown;
        isdPadLeftPressedPrev = dPadLeft;
        isdPadRightPressedPrev = dPadRight;
        islBumperPressedPrev = bumperLeft;
        isrBumperPressedPrev = bumperRight;
        leftStickX = robot.joystickDeadzoneCorrection(opMode.gamepad1.left_stick_x);
        leftStickY = robot.joystickDeadzoneCorrection(-opMode.gamepad1.left_stick_y);
        rightStickX = robot.joystickDeadzoneCorrection(opMode.gamepad1.right_stick_x);
        rightStickY = robot.joystickDeadzoneCorrection(opMode.gamepad1.right_stick_y);
        triggerLeft = opMode.gamepad1.left_trigger;
        triggerRight = opMode.gamepad1.right_trigger;
        aButton = opMode.gamepad1.a;
        bButton = opMode.gamepad1.b;
        xButton = opMode.gamepad1.x;
        yButton = opMode.gamepad1.y;
        dPadUp = opMode.gamepad1.dpad_up;
        dPadDown = opMode.gamepad1.dpad_down;
        dPadLeft = opMode.gamepad1.dpad_left;
        dPadRight = opMode.gamepad1.dpad_right;
        bumperLeft = opMode.gamepad1.left_bumper;
        bumperRight = opMode.gamepad1.right_bumper;

        isaButton2PressedPrev = aButton2;
        isbButton2PressedPrev = bButton2;
        isxButton2PressedPrev = xButton2;
        isyButton2PressedPrev = yButton2;
        isdPadUp2PressedPrev = dPadUp2;
        isdPadDown2PressedPrev = dPadDown2;
        isdPadLeft2PressedPrev = dPadLeft2;
        isdPadRight2PressedPrev = dPadRight2;
        islBumper2PressedPrev = bumperLeft2;
        isrBumper2PressedPrev = bumperRight2;
        leftStickX2 = robot.joystickDeadzoneCorrection(opMode.gamepad2.left_stick_x);
        leftStickY2 = robot.joystickDeadzoneCorrection(-opMode.gamepad2.left_stick_y);
        rightStickX2 = robot.joystickDeadzoneCorrection(opMode.gamepad2.right_stick_x);
        rightStickY2 = robot.joystickDeadzoneCorrection(-opMode.gamepad2.right_stick_y);
        triggerLeft2 = opMode.gamepad2.left_trigger;
        triggerRight2 = opMode.gamepad2.right_trigger;
        aButton2 = opMode.gamepad2.a;
        bButton2 = opMode.gamepad2.b;
        xButton2 = opMode.gamepad2.x;
        yButton2 = opMode.gamepad2.y;
        dPadUp2 = opMode.gamepad2.dpad_up;
        dPadDown2 = opMode.gamepad2.dpad_down;
        dPadLeft2 = opMode.gamepad2.dpad_left;
        dPadRight2 = opMode.gamepad2.dpad_right;
        bumperLeft2 = opMode.gamepad2.left_bumper;
        bumperRight2 = opMode.gamepad2.right_bumper;
    }
}
