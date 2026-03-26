// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;

/** Drive forward then perform launch/intake/launch cycle. */
public class DriveThenShoot extends SequentialCommandGroup {
  /**
   * Drive 4s @0.5, then LaunchSequence 4s, Intake 2s, LaunchSequence 3s
   */
  public DriveThenShoot(CANDriveSubsystem driveSubsystem, CANFuelSubsystem fuelSubsystem) {
    addCommands(
      new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(4),
      new LaunchSequence(fuelSubsystem).withTimeout(4),
      new Intake(fuelSubsystem).withTimeout(2),
      new LaunchSequence(fuelSubsystem).withTimeout(3)
    );
  }
}
