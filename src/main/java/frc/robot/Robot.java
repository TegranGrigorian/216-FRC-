// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
// import edu.wpi.first.math.proto.Controller;
// import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
// import edu.wpi.first.wpilibj.motorcontrol.Talon;
//import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
// import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.autos.midauto2;

import com.ctre.phoenix6.StatusSignal;
//motor and external libraries
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.fasterxml.jackson.databind.cfg.EnumFeature;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.util.Color;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
//camera code
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

import edu.wpi.first.cameraserver.CameraServer;

import java.lang.invoke.TypeDescriptor.OfField;
import java.util.Optional;

import javax.management.loading.PrivateClassLoader;
import javax.print.CancelablePrintJob;

import edu.wpi.first.wpilibj.Encoder;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */

 public class Robot extends TimedRobot {
//Pneumatic Code
  AddressableLED led1 = new AddressableLED(5); // led object
  AddressableLEDBuffer led1Buffer = new AddressableLEDBuffer(1000);
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


  //adressable leds
  private Command m_autonomousCommand;
  private Command m_midAutonCommand;
  private RobotContainer m_robotContainer;
  private Command m_rightAutoCommand0;
  private Command m_rightAutoCommand1;
  private Command m_rightAutoCommand2;
  private Command m_midAutonCommand2;
  private final Joystick operator = new Joystick(1);
  private final Joystick driver = new Joystick(0);
  Thread m_visionThread;
  boolean ledSwitch = false;
  Optional<Alliance> ally = DriverStation.getAlliance();
  boolean ampMode = false;
  boolean kobeMode = false;
  Timer taylorTimer = new Timer();
  Timer hangTime = new Timer();

  //encoder poop 

  Encoder encoder = new Encoder(0, 1);

  //camera code
  
  // private final JoystickButton flywheelOn = new JoystickButton(operator,XboxController.Button.kA.value);
  //private final JoystickButton armMovement = new JoystickButton(driver,XboxController.Button.kRightBumper.value);
  
  /**
   * This function is run when the robot is first started up and shoulutd be used for any
   * initialization code.
   */

   //led poop 
   private void pivotArmUp(double angle) {
    double cAngle = encoder.getDistance();
    double speedProp = (1 - (cAngle / angle)) * .5;
    if (cAngle < angle) {
          leftArm.set(-.15 - speedProp);
          rightArm.set(.15 + speedProp);
          taylorTimer.reset();
          taylorTimer.stop();
        } else {
          leftArm.stopMotor();
          rightArm.stopMotor();
      }
    if (cAngle >= angle) {
      leftArm.stopMotor();
      rightArm.stopMotor();
    }
   }
   private void pivotArmDown(double angle) {
    double cAngle = encoder.getDistance();
    if (cAngle > angle) {
          leftArm.set(.6);
          rightArm.set(-.6);
          taylorTimer.reset();
          taylorTimer.stop();
        } else {
          leftArm.stopMotor();
          rightArm.stopMotor();
      }
    if (cAngle <= angle) {
      leftArm.stopMotor();
      rightArm.stopMotor();
    }
   }
    private void orangeLed() {
      for (var i = 0; i < led1Buffer.getLength(); i++) {
      led1Buffer.setLED(i,Color.kOrange);
      }
      led1.setData(led1Buffer);

  }
  private void purpleLed() {
    for (var i = 0; i < led1Buffer.getLength(); i++) {
      // led1Buffer.setHSV(i, 0, 100, 100);
      led1Buffer.setLED(i,Color.kPurple);
    }
   led1.setData(led1Buffer);
  }
  
  private void redLed() {
    for (var i = 0; i < led1Buffer.getLength(); i++) {
      // led1Buffer.setHSV(i, 0, 100, 100);
      led1Buffer.setLED(i,Color.kRed);
    }
   
   led1.setData(led1Buffer);
  }

  private void blueLed() {
    for (var i = 0; i < led1Buffer.getLength(); i++) {
    // led1Buffer.setHSV(i, 0, 100, 100);
    led1Buffer.setLED(i,Color.kBlue);
  }
  led1.setData(led1Buffer);
  }

  private void rainbow() {
    // For every pixel
    int m_rainbowFirstPixelHue = 0;
    for (var i = 0; i < led1Buffer.getLength(); i++) {
      // Calculate the hue - hue is easier for rainbows because the color
      // shape is a circle so only one value needs to precess
      final var hue = (m_rainbowFirstPixelHue + (i * 180  / led1Buffer.getLength())) % 180;
      // Set the value
      led1Buffer.setHSV(i, hue, 255, 128);
    // m_rainbowFirstPixelHue =  i /2;
    }
    // Increase by to make the rainbow "move"
    m_rainbowFirstPixelHue += 300000000;
    // Check bounds
    m_rainbowFirstPixelHue %= 18;
  }
  private void greenLed() {
      for (var i = 0; i < led1Buffer.getLength(); i++) {
      // led1Buffer.setHSV(i, 0, 100, 100);
      led1Buffer.setLED(i,Color.kGreen);
    }
   
   led1.setData(led1Buffer);
  }
    private void lightPinkLed() {
    for (var i = 0; i < led1Buffer.getLength(); i++) {
      // led1Buffer.setHSV(i, 0, 100, 100);
      led1Buffer.setLED(i,Color.kDeepPink);
    }
   
   led1.setData(led1Buffer);
  }
  private void off() {
  for (var i = 0; i < led1Buffer.getLength(); i++) {
    led1Buffer.setLED(i,Color.kBlack);
  }
  }
  private void hsv0() {
  for (var i = 0; i < led1Buffer.getLength(); i++) {
    led1Buffer.setLED(i,Color.kBlack);
  }
  led1.setData(led1Buffer);
  }
  private void cyanLed() {
    for (var i = 0; i < led1Buffer.getLength(); i++) {
    led1Buffer.setLED(i,Color.kCyan);
  }
  }
  private void flashGreen() {
  for (var i = 0; i < led1Buffer.getLength() + 10; i++) {
    led1Buffer.setLED(i,Color.kGreen);
    led1Buffer.setLED(i - 10, Color.kBlack);
    if (i == led1Buffer.getLength() + 10) {
      i = 0;
    }
  }
  }

  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    CameraServer.startAutomaticCapture("camera", 0);
    m_robotContainer = new RobotContainer();
    m_compressor.enableDigital();
    led1.setLength(led1Buffer.getLength());
    //LED Code
    //send led data
    encoder.reset();
    led1.setData(led1Buffer);
    led1.start();

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
    SmartDashboard.putNumber("Encoder Distance",encoder.getDistance());
    SmartDashboard.putNumber("Auton Timer",time1.get());
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {    

  }

  @Override
  public void disabledPeriodic() {
    //rainbow();
    led1.setData(led1Buffer);
  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    time1.start();

    Optional<Alliance> ally = DriverStation.getAlliance();
    if (ally.get() == Alliance.Red) {
      redLed();
    }
    if (ally.get() == Alliance.Blue) {
      blueLed();
    }
    leftFlywheel.set(.8);
    rightFlywheel.set(.8);    
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    m_midAutonCommand = m_robotContainer.midAutonCommand();
    m_rightAutoCommand0 = m_robotContainer.rightAutoCommand0();
    m_rightAutoCommand1 = m_robotContainer.rightautoCommand1();
    m_rightAutoCommand2 = m_robotContainer.rightAutoCommand2();
    m_midAutonCommand2 = m_robotContainer.midAutonCommand2();
    // schedule the autonomous command (example)

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule(); //left side autonomous command
      
    }    
  
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // if (m_robotContainer.m_chooser.getSelected() == m_midAutonCommand) {
        //Beginning Mid autonomous code


      if (time1.get() > .5 && time1.get() < 1.4) {
        index1.set(1);
        index2.set(1); 
        intake1.set(-1);
        intake2.set(-1);
      }
      if (time1.get() > 1.4 && time1.get() < 3.4){
        index1.set(-.4);
        index2.set(-.4);
      } 
      if (time1.get() > 3 && time1.get() < 3.4){
         m_autonomousCommand.cancel();
      }
      if (time1.get() > 3.5 && time1.get() < 4.6) {
        // intake1.set(.4);
        // intake2.set(.4);
        index1.stopMotor();
        index2.stopMotor();
        pivotArmUp(162.25);
        leftFlywheel.set(.8);
        rightFlywheel.set(.8);
      } if ((time1.get() > 4.6 && time1.get() < 5) && encoder.getDistance() > 135) {
        index1.set(1);
        index2.set(1);
        leftArm.stopMotor();
        rightArm.stopMotor();
      }
      if ((time1.get() > 5.3 && time1.get() < 8)) {
        if (m_midAutonCommand2 != null) {
          m_midAutonCommand2.schedule();
        }
        index1.set(-.05);
        index2.set(-.05);
        pivotArmDown(10);
        off();
      }
      if ((time1.get() > 7 && time1.get() < 9)) {
        index1.stopMotor();
        index2.stopMotor();
      }if ((time1.get() > 8.5 && time1.get() < 9)) {
        m_midAutonCommand2.cancel();

      } 
      if ((time1.get() > 9 && time1.get() < 9.5) && encoder.getDistance() > 140) {
        index1.set(1);
        index2.set(1);
        leftArm.stopMotor();
        rightArm.stopMotor();
      }if ((time1.get() > 10 && time1.get() < 12)) {
        off();
      }
      // if (time1.get() > )


      // else if (time1.get() > 4.7 && time1.get() < 5.5){
      //   index1.set(1);
      //   index2.set(1);
      //   leftFlywheel.set(.8);
      //   rightFlywheel.set(.8);
      // } 
      // else if (time1.get() > 6 && time1.get() < 6.4) {
      //   index1.set(-.3);
      //   index2.set(-.3);
      // } else if (time1.get() > 6.5 && time1.get() < 7.0) {
      //   m_midAutonCommand2.schedule();
      // } else if (time1.get() > 11.8 && time1.get() < 12.3) {
      //   index1.set(1);
      //   index2.set(1);
      // }


      // if (time1.get() > 1 && time1.get() < 2.5) {
      //   pivotArmUp(138.75);
      // } else {
      //   index1.set(1);
      //   index2.set(1);
      // }
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
    time2.reset();

  }
  

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("matchtime", time2.get());
    SmartDashboard.putNumber("Taylor timer 😎", taylorTimer.get());
    s1.set(hangToggle);
    s2.set(hangToggle);
    if (driver.getRawButton(PS4Controller.Button.kL1.value)) {
      redLed();
    } else if (encoder.getDistance() > 418  ) {
      purpleLed();
      operator.setRumble(RumbleType.kBothRumble, .5);
    } else if ((encoder.getDistance() > 90 && encoder.getDistance() < 120) && kobeMode) {
      cyanLed();
    } else if (time2.get() > 120) {
      rainbow();
    }else {
      off();
    }

    if (operator.getPOV() == 0) {
        if (encoder.getDistance() < 420.25) {
          leftArm.set(-1);
          rightArm.set(1);
          taylorTimer.reset();
          taylorTimer.stop();
        } else {
          leftArm.stopMotor();
          rightArm.stopMotor();
          taylorTimer.start();  
                  
          //blueLed();
          ampMode = true;
      }
    }else if (operator.getPOV() == 180) {
        if (encoder.getDistance() > 5) {
          leftArm.set(1);
          rightArm.set(-1);
          taylorTimer.reset();
          taylorTimer.stop();
        } if (encoder.getDistance() < 5) {
          leftArm.stopMotor();
          rightArm.stopMotor();
          ampMode = false;
        
      }
       
    }else if (operator.getRawButton(XboxController.Button.kStart.value)) {
      if (kobeMode == false) {
        if (encoder.getDistance() < 100) {
          leftArm.set(-.15);
          rightArm.set(.15);
          taylorTimer.reset();
          taylorTimer.stop();
        } else {
          leftArm.stopMotor();
          rightArm.stopMotor();
          taylorTimer.start();          
          //blueLed();
          kobeMode = true;
        }
      } else {
        if (encoder.getDistance() > 0) {
          leftArm.set(.15);
          rightArm.set(-.15);
          taylorTimer.reset();
          taylorTimer.stop();
        } if (encoder.getDistance() < 0) {
          leftArm.stopMotor();
          rightArm.stopMotor();
          kobeMode = false;
        }
      }
      
    
    } else if (operator.getPOV() == 90){
      pivotArmUp(164);
      if (encoder.get() > 160) {
        greenLed();  
      }
      
    } else if (operator.getRawAxis(XboxController.Axis.kLeftTrigger.value) > .1) {
      leftArm.set(operator.getRawAxis(XboxController.Axis.kLeftTrigger.value)*.6);
      rightArm.set(-operator.getRawAxis(XboxController.Axis.kLeftTrigger.value)*.6);
    } else if (operator.getRawAxis(XboxController.Axis.kRightTrigger.value) > .1) {
      leftArm.set(-operator.getRawAxis(XboxController.Axis.kRightTrigger.value)*.6);
      rightArm.set(operator.getRawAxis(XboxController.Axis.kRightTrigger.value)*.6);
    } else { 
      leftArm.stopMotor();
      rightArm.stopMotor();
    }
      
    if (operator.getRawButton(XboxController.Button.kLeftBumper.value) && hangToggle == false) {
      hangToggle = true;
      hangTime.start();
    } if (driver.getRawButton(PS4Controller.Button.kSquare.value)){
      hangToggle = false;
    }
    if (driver.getRawAxis(PS4Controller.Axis.kL2.value) > .1) {
      index1.set(-.75);
      index2.set(-.75);
    } else if (driver.getRawAxis(PS4Controller.Axis.kR2.value) > .1) {
      index1.set(.75);
      index2.set(.75);
    // }else if (driver.getRawButton(PS4Controller.Button.kCircle.value)){
    //   index1.set(.25);
    //   index2.set(.25);
    } else {
      index1.stopMotor();
      index2.stopMotor();
    }
    if (driver.getRawButton(PS4Controller.Button.kCircle.value)) {
      intake1.set(1);
      intake2.set(1);
    } else if (driver.getRawButton(PS4Controller.Button.kR1.value) && (encoder.getDistance() > -100 && encoder.getDistance() < 100)) {
      intake1.set(-1);
      intake2.set(-1);
    }
    else {
      intake1.stopMotor();
      intake2.stopMotor();
    }
    if (operator.getRawButton(XboxController.Button.kA.value)) {
      leftFlywheel.set(.75);//.65
      rightFlywheel.set(.75);//.65
    } else if (operator.getRawButton(XboxController.Button.kB.value)) {
      leftFlywheel.set(-.4);
      rightFlywheel.set(-.4);
  
    } else if (operator.getRawButton(XboxController.Button.kY.value)){
      leftFlywheel.set(.18);
      rightFlywheel.set(.32);
      index1.set(.30);
      index2.set(.30);
    } else if (operator.getPOV() == 90) { 
      leftFlywheel.set(.75);
      rightFlywheel.set(.75);
    } else {
      leftFlywheel.stopMotor();
      rightFlywheel.stopMotor();

    }
    led1.setData(led1Buffer);
    if (operator.getRawButton(XboxController.Button.kBack.value)) {
      encoder.reset();
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
