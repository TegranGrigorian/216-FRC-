package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.Constants;

public class ArmSubsystem extends SubsystemBase {
    private final TalonFX armMotor;
    private final Encoder armEncoder;

    // Constants for arm control
    private static final double ENCODER_TICKS_PER_DEGREE = 2048.0 / 360.0; // change this to ur aencoder
    private static final double ARM_HOLD_SPEED = 0.1; // Speed to hold the arm in place (low power)
    private static final double ARM_MAX_SPEED = 0.75; // Max speed so we dont get a bite of 216(was that the bite of 87!!?!?!?)

    public ArmSubsystem() {
        armMotor = new TalonFX(Constants.Arm.LEFT_ARM_CAN_PORT);
        armEncoder = new Encoder(Constants.Arm.ARM_ENCODER_CHANNEL_A, Constants.Arm.ARM_ENCODER_CHANNEL_B);
        armEncoder.reset();
    }

    /**
     * Moves the arm at a given speed for a specific amount of time.
     * @param speed Speed to set (-1.0 to 1.0).
     * @param time Time in seconds to run the motor.
     */
    
    public void setArm(double speed, double time) {
        Timer timer = new Timer();
        timer.start();
        armMotor.set(Math.min(speed, ARM_MAX_SPEED));

        while (timer.get() < time) {
            //i should of made this code better
        }

        armMotor.stopMotor();
    }

    /**
     * Moves the arm to a specific angle.
     * @param targetAngle Target angle in degrees.
     */
    public void moveToAngle(double targetAngle) {
        double currentAngle = getArmAngle();
        double error = targetAngle - currentAngle;

        // Simple proportional control (P-Control)
        double kP = 0.02; // Adjust this constant for your system
        double speed = kP * error;

        // Limit the speed to avoid overshooting
        speed = Math.max(-ARM_MAX_SPEED, Math.min(speed, ARM_MAX_SPEED));

        // Run motor until close enough to the target angle
        while (Math.abs(error) > 1.0) { // 1-degree tolerance
            armMotor.set(speed);
            currentAngle = getArmAngle();
            error = targetAngle - currentAngle;
            speed = kP * error;
            speed = Math.max(-ARM_MAX_SPEED, Math.min(speed, ARM_MAX_SPEED));
        }

        armMotor.stopMotor();
    }
    public void moveToAngleWithTime(double targetAngle, double timeInSeconds) {
        double currentAngle = getArmAngle();
        double angleDifference = targetAngle - currentAngle;

        // Calculate the speed to cover the distance in the given time
        double requiredSpeed = angleDifference / timeInSeconds;
        requiredSpeed = Math.max(-ARM_MAX_SPEED, Math.min(requiredSpeed, ARM_MAX_SPEED));

        Timer timer = new Timer();
        timer.start();

        while (timer.get() < timeInSeconds) {
            armMotor.set(requiredSpeed);
        }

        armMotor.stopMotor();
    }
    /**
     * Holds the arm in its current position with low power.
     */
    public void holdPosition() {
        armMotor.set(ARM_HOLD_SPEED);
    }
    /**
     * Gets the current angle of the arm.
     * @return The arm angle in degrees.
     */
    public double getArmAngle() {
        return armEncoder.getDistance() / ENCODER_TICKS_PER_DEGREE;
    }

    public void resetEncoder() {
        armEncoder.reset();
    }
    public void setSpeed(double speed) {
        armMotor.set(speed);
    }
    public void stopArm() {
        armMotor.stopMotor();
    }
}
