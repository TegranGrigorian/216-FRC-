package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.Timer;
public class IntakeSubsystem extends SubsystemBase {
    private final Spark intakeMotor;

    public IntakeSubsystem() {
        intakeMotor = new Spark(0);  // Example motor controller port
    }

    // Autonomous action: Run the intake at a specified speed for a certain time
    public void runIntakeAutonomous(double speed, double timeInSeconds) {
        Timer time = new Timer();
        intakeMotor.set(speed);
        time.start();
        time.delay(timeInSeconds);
        intakeMotor.stopMotor();
    }

    // Autonomous action: Reverse the intake at a specified speed for a certain time
    public void reverseIntakeAutonomous(double speed, double timeInSeconds) {
        Timer time = new Timer();
        intakeMotor.set(-speed);
        time.start();
        time.delay(timeInSeconds);
        intakeMotor.stopMotor();
    }

    // Optionally, you can add other autonomous methods like stopIntakeAutonomous() if needed.
    public void stopIntake() {
        intakeMotor.stopMotor();
    }
}
