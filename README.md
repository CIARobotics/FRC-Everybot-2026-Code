# FRC--Everybot-2026-Code

Version 1.0.1

Documentation for code coming soon
 
## Controller mappings

Default controller ports (Driver Station > Joystick tab):

- Driver controller: port 0
- Operator controller: port 1

Driver (primary)
- Left stick / Right stick: drive (default command)

Operator (co-driver)
- Left bumper: intake (hold)
- Right bumper: launch sequence (hold)
- A button: eject (hold)
- D-pad Up: climb up (hold)
- D-pad Down: climb down (hold)

Notes:
- To change which controller a command is bound to, edit `src/main/java/frc/robot/RobotContainer.java` and switch calls between `driverController` and `operatorController` in `configureBindings()`.
- If you'd like toggles instead of hold behavior (for example, press once to start intake, press again to stop), I can change those bindings to use toggle commands.

## Autonomous modes

Available autonomous routines (select from the Driver Station `Autonomous` chooser or add the chooser widget to Shuffleboard):

- DriveShootThenClimb (default):
	- Climb down for 6.5 seconds.
	- After 2.5 seconds, drive forward at 0.5 for 4 seconds (overlaps with climb down).
	- Launch for 4 seconds.
	- Climb up for 4 seconds.

- DriveOnly (4s):
	- Drive forward at 0.5 for 4 seconds.

- DriveThenShoot:
	- Drive forward at 0.5 for 4 seconds, then LaunchSequence 4s, Intake 2s, LaunchSequence 3s.

Notes about autonomous and debugging widgets:

- The code writes live telemetry to SmartDashboard / Shuffleboard for debugging:
	- `Auto Step` (string) — current phase name (e.g., "Launching").
	- `Auto Step Duration` (number) — expected duration in seconds for the current phase.
	- `Auto Time Remaining` (number) — live countdown of seconds left for the current phase.

- To pick an autonomous routine before a match:
	1. Open Driver Station -> Autonomous and select the desired routine from the SendableChooser.
	2. (Optional) Open Shuffleboard and add the SendableChooser widget on an "Autonomous" tab for a dashboard-based selector.

- If you want the chooser programmatically added to Shuffleboard by default, I can add that (it currently places static labels/widgets). I can also add extra telemetry (motor currents, RPMs) to the Autonomous tab if you want more visibility.

## How to build and test locally

From a PowerShell prompt in the project root run:

```powershell
.\gradlew.bat build -x test
```

This will compile the robot code without running tests. Deploy to the robot as usual from VS Code / Gradle once the build succeeds.
