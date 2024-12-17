package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.autos.Old.*;

import frc.robot.commands.*;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */

 // I also added comments because this code is really confusing
public class RobotContainer {
    /* Controllers */
    
    SendableChooser<Command> m_chooser = new SendableChooser<>();
    private final Joystick driver = new Joystick(0);
    
    /* Drive Controls */
    private final int translationAxis = PS4Controller.Axis.kLeftY.value;
    private final int strafeAxis = PS4Controller.Axis.kLeftX.value;
    private final int rotationAxis = PS4Controller.Axis.kRightX.value;

    /* Driver Buttons */
    private final JoystickButton zeroGyro = new JoystickButton(driver, PS4Controller.Button.kTriangle.value);
    private final JoystickButton robotCentric = new JoystickButton(driver, PS4Controller.Button.kL1.value);
    private final JoystickButton armTo45Button = new JoystickButton(driver, PS4Controller.Button.kCircle.value); // A button for 45 degrees
    private final JoystickButton armToZeroButton = new JoystickButton(driver, PS4Controller.Button.kSquare.value); // B button for 0 degrees
    private final POVButton armMoveUpButton = new POVButton(driver, 0); // POV up to move arm with speed
    private final POVButton armMoveDownButton = new POVButton(driver, 180); // POV down to move arm with speed

    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();
    private final ArmSubsystem armSubsystem = new ArmSubsystem(); // Create instance of ArmSubsystem
    
    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve, 
                () -> -driver.getRawAxis(translationAxis), 
                () -> -driver.getRawAxis(strafeAxis), 
                () -> -driver.getRawAxis(rotationAxis), 
                () -> robotCentric.getAsBoolean()
            )
        );
        
        // Configure the button bindings
        configureButtonBindings();

        // Add commands to the autonomous command chooser
        m_chooser.setDefaultOption("Default", getAutonomousCommand()); // set default auton
        m_chooser.addOption("Mid", midAutonCommand());
        
        // Put the chooser on the dashboard and send the data
        SmartDashboard.putData(m_chooser);
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));

        // Arm control button bindings
        armTo45Button.onTrue(new InstantCommand(() -> armSubsystem.moveToAngle(45))); // Move arm to 45 degrees on button press
        armToZeroButton.onTrue(new InstantCommand(() -> armSubsystem.moveToAngle(0))); // Move arm to 0 degrees on button press
        
        // POV buttons to control arm speed
        armMoveUpButton.onTrue(new InstantCommand(() -> armSubsystem.setArm(0.5, 0))); // Move arm up with speed (adjust speed as needed)
        armMoveDownButton.onTrue(new InstantCommand(() -> armSubsystem.setArm(-0.5, 0))); // Move arm down with speed (adjust speed as needed)
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return m_chooser.getSelected(); // send an autonomous command that is determined by the selector's output
    }

    public Command midAutonCommand() {
        return new midauto(s_Swerve);
    }

    public Command rightautoCommand1() {
        return new rightsideauto2(s_Swerve);
    }

    public Command rightAutoCommand2() {
        return new rightsideauto3(s_Swerve);
    }
}
