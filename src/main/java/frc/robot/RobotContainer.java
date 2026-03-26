// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import static frc.robot.Constants.OperatorConstants.*;

import frc.robot.commands.ClimbDown;
import frc.robot.commands.ClimbUp;
import frc.robot.commands.Drive;
import frc.robot.commands.Eject;
import frc.robot.commands.DriveShootThenClimb;
import frc.robot.commands.Intake;
import frc.robot.commands.LaunchSequence;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;
import frc.robot.subsystems.ClimberSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem();
  private final CANFuelSubsystem fuelSubsystem = new CANFuelSubsystem();
  private final ClimberSubsystem climberSubsystem = new ClimberSubsystem();

  // The driver's controller
  private final CommandXboxController driverController = new CommandXboxController(
      DRIVER_CONTROLLER_PORT);

  // The operator's controller, by default it is setup to use a single controller
  private final CommandXboxController operatorController = new CommandXboxController(
      OPERATOR_CONTROLLER_PORT);

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();
  // Shuffleboard entry to enable/disable operator controls at runtime
  private final GenericEntry operatorControlsEnabledEntry;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
  // Create Shuffleboard widgets for controller mappings and operator control toggle
  Shuffleboard.getTab("Operator")
    .add("Controller Mappings",
      "Driver: Left/Right sticks = drive\nOperator: LB=intake, RB=launch, A=eject, DPadUp=climb up, DPadDown=climb down")
    .withSize(6, 2);

  operatorControlsEnabledEntry = Shuffleboard.getTab("Operator")
    .add("Operator Controls Active", true)
    .withPosition(0, 2)
    .withSize(2, 1)
    .getEntry();

  // Put a mirror value on SmartDashboard too (some existing code reads SmartDashboard)
    
  configureBindings();

    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
  autoChooser.setDefaultOption("DriveShootThenClimb (Climb+Drive+Shoot)", new DriveShootThenClimb(driveSubsystem, fuelSubsystem, climberSubsystem));
  // Add additional autonomous modes
  autoChooser.addOption("DriveOnly (4s)", new frc.robot.commands.DriveOnly(driveSubsystem));
  autoChooser.addOption("DriveThenShoot (Drive->4s Launch->2s Intake->3s Launch)", new frc.robot.commands.DriveThenShoot(driveSubsystem, fuelSubsystem));

  // Shuffleboard entries for autonomous debugging
  // Add the SendableChooser so you can pick the autonomous from Shuffleboard/Driver Station
  Shuffleboard.getTab("Autonomous").add("Auto Chooser", autoChooser).withSize(2,2).withPosition(0,0);
  Shuffleboard.getTab("Autonomous").add("Auto Step", "Idle").withSize(3,1).withPosition(2,0);
  Shuffleboard.getTab("Autonomous").add("Auto Step Duration", 0).withSize(2,1).withPosition(2,1);
  Shuffleboard.getTab("Autonomous").add("Auto Time Remaining", 0).withSize(2,1).withPosition(4,1);

  // Telemetry tab: mirror some SmartDashboard entries here for easy viewing
  Shuffleboard.getTab("Telemetry").add("Intaking feeder roller value", SmartDashboard.getNumber("Intaking feeder roller value", 0)).withSize(2,1).withPosition(0,0);
  Shuffleboard.getTab("Telemetry").add("Intaking intake roller value", SmartDashboard.getNumber("Intaking intake roller value", 0)).withSize(2,1).withPosition(2,0);
  Shuffleboard.getTab("Telemetry").add("Launching feeder roller value", SmartDashboard.getNumber("Launching feeder roller value", 0)).withSize(2,1).withPosition(0,1);
  Shuffleboard.getTab("Telemetry").add("Launching launcher roller value", SmartDashboard.getNumber("Launching launcher roller value", 0)).withSize(2,1).withPosition(2,1);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the {@link Trigger#Trigger(java.util.function.BooleanSupplier)}
   * constructor with an arbitrary predicate, or via the named factories in
   * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
   * for {@link CommandXboxController Xbox}/
   * {@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {

    // Decide whether operator controller (co-driver) handles the controls or the driver
    boolean operatorActive = true;
    if (operatorControlsEnabledEntry != null) {
      operatorActive = operatorControlsEnabledEntry.getBoolean(true);
    }
    // Mirror on SmartDashboard for compatibility with existing code/UI
    SmartDashboard.putBoolean("Operator Controls Active", operatorActive);

    // While the left bumper is held, intake Fuel
    if (operatorActive) {
      operatorController.leftBumper().whileTrue(new Intake(fuelSubsystem));
      // While the right bumper on the operator controller is held, spin up for 1
      // second, then launch fuel. When the button is released, stop.
      operatorController.rightBumper().whileTrue(new LaunchSequence(fuelSubsystem));
      // While the A button is held on the operator controller, eject fuel back out
      // the intake
      operatorController.a().whileTrue(new Eject(fuelSubsystem));
      // While the down arrow on the directional pad is held it will unclimb the robot
      operatorController.povDown().whileTrue(new ClimbDown(climberSubsystem));
      // While the up arrow on the directional pad is held it will climb the robot
      operatorController.povUp().whileTrue(new ClimbUp(climberSubsystem));
    } else {
      driverController.leftBumper().whileTrue(new Intake(fuelSubsystem));
      driverController.rightBumper().whileTrue(new LaunchSequence(fuelSubsystem));
      driverController.a().whileTrue(new Eject(fuelSubsystem));
      driverController.povDown().whileTrue(new ClimbDown(climberSubsystem));
      driverController.povUp().whileTrue(new ClimbUp(climberSubsystem));
    }

    // Set the default command for the drive subsystem to the command provided by
    // factory with the values provided by the joystick axes on the driver
    // controller. The Y axis of the controller is inverted so that pushing the
    // stick away from you (a negative value) drives the robot forwards (a positive
    // value)
    driveSubsystem.setDefaultCommand(new Drive(driveSubsystem, driverController));

    fuelSubsystem.setDefaultCommand(fuelSubsystem.run(() -> fuelSubsystem.stop()));

    climberSubsystem.setDefaultCommand(climberSubsystem.run(() -> climberSubsystem.stop()));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}
