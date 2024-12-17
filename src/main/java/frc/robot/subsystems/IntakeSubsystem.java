package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
    private final Spark intakeMotor;
    private final Encoder intakeEncoder;
    private final DigitalInput laserBreakSensor; // Laser break sensor for stopping intake

    private boolean isIntakeRunning = false;

    // Constants for intake system
    private static final double MAX_INTAKE_SPEED = 1.0;
    private static final double MIN_INTAKE_SPEED = 0.1;
    private static final double INTAKE_SLOW_SPEED = 0.3;

    public IntakeSubsystem() {
        intakeMotor = new Spark(Constants.Intake.INTAKE_PWM_PORT);
        intakeEncoder = new Encoder(Constants.Intake.INTAKE_ENCODER_CHANNEL_A, Constants.Intake.INTAKE_ENCODER_CHANNEL_B);
        laserBreakSensor = new DigitalInput(Constants.Intake.LASER_BREAK_SENSOR_PORT);
        intakeEncoder.reset();
    }

    public void runIntake(double speed) {
        double safeSpeed = Math.max(-MAX_INTAKE_SPEED, Math.min(speed, MAX_INTAKE_SPEED)); // Limit speed
        intakeMotor.set(safeSpeed);
        isIntakeRunning = true;
    }

    public void stopIntake() {
        intakeMotor.stopMotor();
        isIntakeRunning = false;
    }

    public void toggleIntake(double speed) {
        if (isIntakeRunning) {
            stopIntake();
        } else {
            runIntake(speed);
        }
    }

    public void reverseIntake(double speed) {
        runIntake(-Math.abs(speed));
    }

    // Run the intake for a specific amount of time
    public void runIntakeForTime(double speed, double timeInSeconds) {
        Timer timer = new Timer();
        timer.start();
        runIntake(speed);

        while (timer.get() < timeInSeconds) {
        }

        stopIntake();
    }

    public boolean isLaserBreakTriggered() {
        return !laserBreakSensor.get(); // Assuming a normally open sensor (triggers when blocked)
    }

    public void stopIntakeIfLaserBlocked() {
        if (isLaserBreakTriggered()) {
            stopIntake();
        }
    }

    public double getIntakeDistance() {
        return intakeEncoder.getDistance();
    }

    public double getIntakeSpeed() {
        return intakeEncoder.getRate();
    }

    public void resetEncoder() {
        intakeEncoder.reset();
    }

    public boolean isIntakeRunning() {
        return isIntakeRunning;
    }

    public void safetyCheck() {
        if (isIntakeRunning && isLaserBreakTriggered()) {
            stopIntake();
        }
    }
}
