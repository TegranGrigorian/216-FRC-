// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
// import edu.wpi.first.math.proto.Controller;
// import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
// import edu.wpi.first.wpilibj.motorcontrol.Talon;
//import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
// import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.WaitCommand;
//motor and external libraries
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */

 public class Robot extends TimedRobot {
//Pneumatic Code
  private final Compressor m_compressor = new Compressor(PneumaticsModuleType.CTREPCM);
  private final Solenoid s1 = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
  private final Solenoid s2 = new Solenoid(PneumaticsModuleType.CTREPCM,7);
  TalonFX leftFlywheel = new TalonFX(5);
  TalonFX rightFlywheel = new TalonFX(10);
  TalonFX leftArm = new TalonFX(2);
  TalonFX rightArm = new TalonFX(1);
  Spark index1 = new Spark(0);
  Spark index2 = new Spark(1);
  Spark intake1 = new Spark(2);
  Spark intake2 = new Spark(3);
  private Timer time2 = new Timer();
  private Timer time1 = new Timer();
  private boolean autonSwitch = false;
  private boolean hangToggle = false;
  //private final Joystick driver = new Joystick(0);
  public static final CTREConfigs ctreConfigs = new CTREConfigs();

  private Command m_autonomousCommand;
  private Command m_midAutonCommand;
  private RobotContainer m_robotContainer;
  private Command m_rightAutoCommand0;
  private Command m_rightAutoCommand1;
  private Command m_rightAutoCommand2;
  private final Joystick operator = new Joystick(1);
  private final Joystick driver = new Joystick(0);

  // private final JoystickButton flywheelOn = new JoystickButton(operator,XboxController.Button.kA.value);
  //private final JoystickButton armMovement = new JoystickButton(driver,XboxController.Button.kRightBumper.value);
  
  /**
   * This function is run when the robot is first started up and shoulutd be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    m_compressor.enableDigital();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
   
    time1.start();
    leftFlywheel.set(.8);
    rightFlywheel.set(.8);
    new WaitCommand(3);
    intake1.set(-1);
    intake2.set(-1);
    
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    m_midAutonCommand = m_robotContainer.midAutonCommand();
    m_rightAutoCommand0 = m_robotContainer.rightAutoCommand0();
    m_rightAutoCommand1 = m_robotContainer.rightautoCommand1();
    m_rightAutoCommand2 = m_robotContainer.rightAutoCommand2();
    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule(); //left side autonomous command

      //Mid Auton
      // m_midAutonCommand.schedule();

      //Right Side Auton
      // m_rightAutoCommand0.schedule();
      //m_rightAutoCommand1.schedule();
    }    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    if (m_robotContainer.m_chooser.getSelected() == m_midAutonCommand) {
        //Beginning Mid autonomous code
      if (time1.get() > .7 && time1.get() < 1.2) {
        index1.set(1);
        index2.set(1); 
      }
      if (time1.get() > 1.5 && time1.get() < 2.5){
        index1.stopMotor();
        index2.stopMotor();
        
      }
      else if (time1.get() > 4.5 && time1.get() < 5.5){
        index1.set(1);
        index2.set(1);
      } 
      else if (time1.get() > 5.8 && time1.get() < 8) {
        leftFlywheel.stopMotor();
        rightFlywheel.stopMotor();
        index1.stopMotor();
        index2.stopMotor();
        intake1.stopMotor();
        intake2.stopMotor();
      }
    }
    
    if (m_robotContainer.m_chooser.getSelected() == m_autonomousCommand) {
      // begining left side Autonomus code
      if (time1.get() > 1 && time1.get() < 1.1 ) {
        index1.set(.75);
        index2.set(.75); 
      } else if(time1.get()>1.2 && time1.get()<4.9){
        index1.stopMotor();
        index2.stopMotor();
      }else if(time1.get() >6.5 && time1.get()<10.5) {
        index1.set(.75);
        index2.set(.75);
      }
      // end of begining left side autonomus code
    }
    if (m_robotContainer.m_chooser.getSelected() == m_rightAutoCommand0) {
      // // beggining of right side auton
      if (time1.get() < 2) {
        index1.set(1);
        index2.set(1);
      }
      if (time1.get() > 3.5 && time1.get() < 4){
        leftFlywheel.set(.5);
        rightFlywheel.set(.5);
      }
      if (time1.get() > 7 && autonSwitch == false && time1.get() < 10){
        autonSwitch = true;
        m_rightAutoCommand1.schedule();
      }
      if (time1.get() > 10.5 && autonSwitch == true){
        autonSwitch = false;
        m_rightAutoCommand2.schedule();
      }
    }
    
  }
    

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    leftArm.setNeutralMode(NeutralModeValue.Brake);
    rightArm.setNeutralMode(NeutralModeValue.Brake);
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
      
    }
    time2.start();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
      SmartDashboard.putNumber("matchtime", time2.get());
    s1.set(hangToggle);
    s2.set(hangToggle);
    if (operator.getRawButton(XboxController.Button.kRightBumper.value) && hangToggle == false) {
      hangToggle = true;
      new WaitCommand(.2);
    }
    if (operator.getRawButton(XboxController.Button.kLeftBumper.value) && hangToggle == true) {
      hangToggle = true;
      new WaitCommand(.2);
    }
    if (driver.getRawButton(PS4Controller.Button.kL1.value)) {
      index1.set(-.75);
      index2.set(-.75);
    } else if (driver.getRawButton(PS4Controller.Button.kR1.value)) {
      index1.set(.75);
      index2.set(.75);
    }else if (driver.getRawButton(PS4Controller.Button.kCircle.value)){
      index1.set(.25);
      index2.set(.25);
    } else {
      index1.stopMotor();
      index2.stopMotor();
    }
    if (driver.getRawAxis(PS4Controller.Axis.kL2.value) > .1) {
      intake1.set(1);
      intake2.set(1);
    } else if (driver.getRawAxis(PS4Controller.Axis.kR2.value) > .1) {
      intake1.set(-1);
      intake2.set(-1);
    }
    else {
      intake1.stopMotor();
      intake2.stopMotor();
    }
    if (operator.getRawButton(XboxController.Button.kA.value)) {
      leftFlywheel.set(.85);//.65
      rightFlywheel.set(.85);//.65
    } else if (operator.getRawButton(XboxController.Button.kB.value)) {
      leftFlywheel.set(-.4);
      rightFlywheel.set(-.4);
  
    } else if (operator.getRawButton(XboxController.Button.kY.value)){
      leftFlywheel.set(.15);
      rightFlywheel.set(.32);
    } else if (operator.getRawButton(XboxController.Button.kX.value)){
      leftFlywheel.set(.05);
      rightFlywheel.set(.05);
    }
    else { 

      leftFlywheel.stopMotor();
      rightFlywheel.stopMotor();
    }
    
    if (operator.getRawAxis(XboxController.Axis.kLeftTrigger.value) > .1) {
      leftArm.set(operator.getRawAxis(XboxController.Axis.kLeftTrigger.value)*.6);
      rightArm.set(-operator.getRawAxis(XboxController.Axis.kLeftTrigger.value)*.6);
    } else if (operator.getRawAxis(XboxController.Axis.kRightTrigger.value) > .1) {
      leftArm.set(-operator.getRawAxis(XboxController.Axis.kRightTrigger.value)*.6);
      rightArm.set(operator.getRawAxis(XboxController.Axis.kRightTrigger.value)*.6);
    } else { 
      leftArm.stopMotor();
      rightArm.stopMotor();
    }

  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
