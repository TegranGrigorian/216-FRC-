// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.Swerve;

import java.util.Optional;

import javax.swing.text.html.Option;

//motor and external libraries
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */

 public class Robot extends TimedRobot {
//Pneumatic Code
  /*
   * How to code objects: a private is a object only private to the class it is dervived from for example this one is from class robot
   * so keeping it private menas it is only contain in the robot class, but of course if there is molophorisim and a class integrates
   * the robot public class the object will polymorphate to the class.(welcome to class structure with java :(        )
   * final means it will not change same as static in cpp c or other objects in java that relate to data types, compressor is the object
   * being called and we name it m_compressor then we set it equal to a NEW compressor and that object needs paramaters so we must input
   * a value into it which in this case is the pneumaticsmoduletype.ctrepcn.
   * 
   * Some of them are missing, what is the point of private and final? Back in the old days of coding memory managment was really important
   * if we didnt have memory manage we would have a overflow later on in the stack (this is where stack overflow comes from) so programming
   * laungauges can direct the hardware, the memeory, to store data which is crucial this is what makes a laungaue low level. However
   * if you dont need to change a varbile then if u make it final u just need to find its memory in ram but nothing about changing it so
   * it speeds up processing time(slightly) samething with private, if its not extending to all the classes then it will take less time
   * for other classes to intilaze the varbiles its deriving. This is a really bad explanation but its something if your confused
   */

   /*
    * so what are functions? functions are like the math equivilant f(x) = x, f - is the function, x - is the input or paramater and the 
    = with numerical calculations after word is the functions blocking, sequnce and steps

    void yourmom(int in) {
      System.out.printIn("in"); // im pretty sure syntax is wrong lol idrc its an example
    }

    yourmom(10); // <-the 10 inside is the input, x yourmom is the function name we made and we call the function to perform a task.

    the void is telling the compier that you are making a function, yourmom is the name, int in is a integer data type called in(its the x)
    and the {} have code inside of them that the function will proccess and output. so in this case out put the what the user put in
    to the console

    Difference between final and static, a static is inherited by the classes it is in and can be reinisizled(called again in the class)
    final is a constant that can be called again
    Test this out!
    under the compressor line try to call m_compressor then do a dot to try and find any child function, none will appear
    ex: m_compressor.

    so doing a static final makes sometihng inherit to the class and not able to change
    */

  private final Compressor m_compressor = new Compressor(PneumaticsModuleType.CTREPCM);
  private final Solenoid s1 = new Solenoid(PneumaticsModuleType.CTREPCM, 0); // read documentaion, it tells you what the paramters are and what the function needs
  private final Solenoid s2 = new Solenoid(PneumaticsModuleType.CTREPCM,1);
  TalonFX leftFlywheel = new TalonFX(5); //these arent private cuz im lazy and final not here either cuz again im lazy
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
  public static final CTREConfigs ctreConfigs = new CTREConfigs(); //umm u dont want ur stuf
  Optional<Alliance> ally = DriverStation.getAlliance(); // were gonna do some cool things with this
  private Command m_autonomousCommand; //so the command object typically has a lot of data associated with it so make it private to save memory(if you want :)
  private Command m_midAutonCommand;
  private RobotContainer m_robotContainer;
  private Command m_rightAutoCommand0;
  private Command m_rightAutoCommand1;
  private Command m_rightAutoCommand2;
  private final Joystick operator = new Joystick(1);
  private final Joystick driver = new Joystick(0);
  private static final String kDefaultAuto = "ExampleAuto";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  public Leds ledController;
  private double startTime;
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
    //led initalizations!
    ledController = new Leds(0,100); //0 -> port, 100 -> buffer amount of leds object you wnat to configure
    /*
     * Lets say you have a 1000 led strip, you set the buffer to 900, it will create 900 sub-leds that can be controlled out of the 1000 which means each buffer is 1000/900
     * leds, if this makes sense at all :)
     * DONT OVER DO IT THO CUZ IT TAKES MEMORY TO INCREASE BUFFERS TO HIGH NUMBER!!
     * I recomend set the buffer to the number of leds you have on a strip or lower NOT HIGHER!
     * Ok but not thats all my warnings look at the cool functions I did
     */
    //heres a cool thing you could make the leds do :)
    startTime = Timer.getFPGATimestamp(); // Initialize start time when the robot starts // purple!
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
    //cool led code
    double elapsedTime = Timer.getFPGATimestamp() - startTime;
    if (ally.isPresent()) { //essentially are we connected to an frc field
      if (ally.get() == Alliance.Red) {
        ledController.breatingEffect(0, 255, 128, 3, elapsedTime);
      } else {
        ledController.breatingEffect(120, 255, 128, 3, elapsedTime);
      }
    } else {
      ledController.hollowPurple(120, 0, 270, 255, 128, 3, elapsedTime);
    } 
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    ledController.setRainbow(5, 128, 255);
  }

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
    }    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    
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
      hangToggle = false;
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
