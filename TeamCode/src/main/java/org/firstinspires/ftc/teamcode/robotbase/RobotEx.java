package org.firstinspires.ftc.teamcode.robotbase;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotEx {
    protected final Telemetry telemetry;
    protected final FtcDashboard dashboard;
    protected final Telemetry dashboardTelemetry;

    protected final GamepadEx driverOp;
    protected final GamepadEx toolOp;

    protected final MecanumDriveSubsystem drive;
    protected final MecanumDriveCommand driveCommand;

    protected final IMUSubsystem gyro;
    protected Camera camera;

    protected HeadingControllerSubsystem gyroFollow;
    protected HeadingControllerSubsystem cameraFollow;
    protected final Boolean initCamera;

    protected BalancingSubsystem balancer;

    protected TelemetrySubsystem telemetryEx;

    public RobotEx(HardwareMap hardwareMap, Telemetry telemetry, GamepadEx driverOp,
                   GamepadEx toolOp) {
        this(hardwareMap, telemetry, driverOp, toolOp, false, true,
                false, false, false, false,
                true);
    }

    public RobotEx(HardwareMap hardwareMap, Telemetry telemetry, GamepadEx driverOp,
                   GamepadEx toolOp, Boolean initCamera, Boolean useCameraFollower,
                   Boolean frontLeftInvert, Boolean frontRightInvert, Boolean rearLeftInvert,
                   Boolean rearRightInvert, Boolean useBalancingController) {
        this.initCamera = initCamera;
        ///////////////////////////////////////// Gamepads /////////////////////////////////////////
        this.driverOp = driverOp;
        this.toolOp = toolOp;

        /////////////////////////////////////// Telemetries //////////////////////////////////////
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = dashboard.getTelemetry();
        this.telemetry = telemetry;
        this.telemetryEx = new TelemetrySubsystem(this.telemetry, this.dashboardTelemetry);

        //////////////////////////////////////////// IMU ///////////////////////////////////////////

        gyro = new IMUSubsystem(hardwareMap, this.telemetry, dashboardTelemetry);
        CommandScheduler.getInstance().registerSubsystem(gyro);


        ////////////////////////////////////////// Camera //////////////////////////////////////////
        if (initCamera) {
            camera = new Camera(hardwareMap, dashboard, telemetry,
                    () -> this.driverOp.getButton(GamepadKeys.Button.BACK));
        }

        //////////////////////////////////////// Drivetrain ////////////////////////////////////////
        drive = new MecanumDriveSubsystem(hardwareMap, frontLeftInvert, frontRightInvert,
                rearLeftInvert, rearRightInvert);
        driveCommand = new MecanumDriveCommand(drive, this::drivetrainForward,
                this::drivetrainStrafe, this::drivetrainTurn, gyro::getRawYaw,
                () -> driverOp.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER));

        CommandScheduler.getInstance().registerSubsystem(drive);
        drive.setDefaultCommand(driveCommand);

        /////////////////////////////////////// Gyro Follower //////////////////////////////////////

        gyroFollow = new HeadingControllerSubsystem(gyro::getYaw,
                gyro::findClosestOrientationTarget);
        new Trigger(() -> driverOp.getRightY() >= 0.8).whenActive(
                new InstantCommand(() -> gyroFollow.setGyroTarget(180), gyroFollow));
        new Trigger(() -> driverOp.getRightY() <= -0.8).whenActive(
                new InstantCommand(() -> gyroFollow.setGyroTarget(0), gyroFollow));
        new Trigger(() -> driverOp.getRightX() >= 0.8).whenActive(
                new InstantCommand(() -> gyroFollow.setGyroTarget(-90), gyroFollow));
        new Trigger(() -> driverOp.getRightX() <= -0.8).whenActive(
                new InstantCommand(() -> gyroFollow.setGyroTarget(90), gyroFollow));
//        driverOp.getGamepadButton(GamepadKeys.Button.DPAD_UP)
//                .whenPressed(new InstantCommand(() -> gyroFollow.setGyroTarget(0)));
//        driverOp.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
//                .whenPressed(new InstantCommand(() -> gyroFollow.setGyroTarget(180)));
//        driverOp.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
//                .whenPressed(new InstantCommand(() -> gyroFollow.setGyroTarget(90)));
//        driverOp.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
//                .whenPressed(new InstantCommand(() -> gyroFollow.setGyroTarget(-90)));
        driverOp.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .whenPressed(new InstantCommand(gyroFollow::toggleState, gyroFollow));

        telemetryEx.addMonitor("Left X: ", () -> toolOp.getLeftX());

        telemetryEx.addMonitor("Left Y: ", () -> toolOp.getLeftY());
        telemetryEx.addMonitor("Right X: ", () -> toolOp.getRightX());
        telemetryEx.addMonitor("Right Y: ", () -> toolOp.getRightY());

        /////////////////////////////////// Balancing Controller ///////////////////////////////////
        if (useBalancingController) {
            balancer = new BalancingSubsystem(gyro::getPitch, gyro::getRoll);
            driverOp.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                    .whenPressed(new InstantCommand(balancer::toggleState, balancer));
        }

        ////////////////////////////////////// Camera Follower /////////////////////////////////////
        if (initCamera && useCameraFollower) {
            cameraFollow = new HeadingControllerSubsystem(camera::getObject_x);
            driverOp.getGamepadButton(GamepadKeys.Button.START)
                    .whenPressed(new InstantCommand(cameraFollow::toggleState, cameraFollow));
        }

        ////////////////////////// Setup and Initialize Mechanisms Objects /////////////////////////
        initMechanisms(hardwareMap);
    }

    public void initMechanisms(HardwareMap hardwareMap) {
        // should be overridden by child class
    }

    public double drivetrainStrafe() {
        if (balancer.isEnabled())
            return balancer.getRollCorrection() + driverOp.getLeftX();

        return driverOp.getLeftX();
    }

    public double drivetrainForward() {
        if (balancer.isEnabled())
            return -balancer.getPitchCorrection() + driverOp.getLeftY();

        return driverOp.getLeftY();
    }

    public double drivetrainTurn() {
        if (gyroFollow.isEnabled())
            return -gyroFollow.calculateTurn();
        if (initCamera && cameraFollow.isEnabled())
            return cameraFollow.calculateTurn();

        return driverOp.getRightX();
    }

    public void telemetryUpdate() {
        telemetry.update();
    }

    public void dashboardTelemetryUpdate() {
        dashboardTelemetry.update();
    }
}