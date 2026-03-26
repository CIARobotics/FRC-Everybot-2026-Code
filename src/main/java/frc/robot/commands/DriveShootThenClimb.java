// Copyright (c) FIRST and other WPILab contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;
import frc.robot.subsystems.ClimberSubsystem;

/**
 * Autonomous: Climb down while delayed drive, then launch, then climb up.
 */
public class DriveShootThenClimb extends SequentialCommandGroup {
  /** Creates a new DriveShootThenClimb. */
  public DriveShootThenClimb(CANDriveSubsystem driveSubsystem, CANFuelSubsystem ballSubsystem, ClimberSubsystem climberSubsystem) {
    // Autonomous sequence requested:
    // 1) Run climber down for 6.5 seconds.
    // 2) After 2.5 seconds from start, drive forward for 4 seconds (overlaps with climber down).
    // 3) After climber down completes (at 6.5s), run launcher for 4 seconds to shoot balls.
    // 4) Run climber up for 4 seconds. Then stop.

    addCommands(
        // Step 1 & 2: run climber down for 6.5s while (after 2.5s) driving forward for 4s
        timedPhase(
            new ParallelCommandGroup(
                new ClimbDown(climberSubsystem),
                new SequentialCommandGroup(new WaitCommand(2.5), new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(4))
            ),
            6.5,
            "Climb Down & (delayed) Drive"
        ),

        // Step 3: launch for 4s
        timedPhase(new LaunchSequence(ballSubsystem), 4, "Launching"),

        // Step 4: climb up for 4s
        timedPhase(new ClimbUp(climberSubsystem), 4, "Climb Up"),

        // Done
        new InstantCommand(() -> {
          SmartDashboard.putString("Auto Step", "Finished");
          SmartDashboard.putNumber("Auto Step Duration", 0);
          SmartDashboard.putNumber("Auto Time Remaining", 0);
        })
    );
  }

  private static SequentialCommandGroup timedPhase(Command action, double durationSeconds, String name) {
    return new SequentialCommandGroup(
        new InstantCommand(() -> {
          SmartDashboard.putString("Auto Step", name);
          SmartDashboard.putNumber("Auto Step Duration", durationSeconds);
          SmartDashboard.putNumber("Auto Step Start", Timer.getFPGATimestamp());
        }),
        new ParallelCommandGroup(
            action.withTimeout(durationSeconds),
            new RunCommand(() -> {
              double start = SmartDashboard.getNumber("Auto Step Start", Timer.getFPGATimestamp());
              double remaining = durationSeconds - (Timer.getFPGATimestamp() - start);
              SmartDashboard.putNumber("Auto Time Remaining", Math.max(0.0, remaining));
            }).withTimeout(durationSeconds)
        )
    );
  }
}
