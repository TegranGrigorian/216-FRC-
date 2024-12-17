package frc.robot.commands;

import frc.robot.subsystems.ArmSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class MoveArmToPositionTeleop extends Command {
    private final ArmSubsystem armSubsystem;
    private final double targetAngle;
    private final double timeInSeconds;

    public MoveArmToPositionTeleop(ArmSubsystem armSubsystem, double targetAngle, double timeInSeconds) {
        this.armSubsystem = armSubsystem;
        this.targetAngle = targetAngle;
        this.timeInSeconds = timeInSeconds;
        addRequirements(armSubsystem);
    }

    @Override
    public void initialize() {
        if (timeInSeconds > 0) {
            armSubsystem.moveToAngleWithTime(targetAngle, timeInSeconds);
        } else {
            armSubsystem.moveToAngle(targetAngle);
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
