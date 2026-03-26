// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.CANDriveSubsystem;

/** Simple autonomous that just drives forward for a fixed time. */
public class DriveOnly extends SequentialCommandGroup {
  public DriveOnly(CANDriveSubsystem driveSubsystem) {
    addCommands(
      new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(4)
    );
  }
}
