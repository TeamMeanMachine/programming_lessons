package frc.team2471.lessons

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.Command
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team2471.frc.lib.control.Autonomi
import org.team2471.frc.lib.control.commands.runOnce
import org.team2471.frc.lib.control.commands.runOnceCommand
import org.team2471.frc.lib.control.commands.sequenceCommand


object Autonomous: Autonomi() {
    /** Supplier that sets the robot's pose. Used inside [setDrivePositionToAutoStartPose] */
    override val drivePoseSetter: (Pose2d) -> Unit = { /* noop */ }

    /** Warmup function for speeding up auto loop times. Runs when selected auto changes. */
    override val warmupFunction: () -> Unit = {
        println("warmup")
    }

    /** Chooser for selecting autonomous commands */
    override val autoChooser: LoggedDashboardChooser<AutoCommand?> =
        LoggedDashboardChooser<AutoCommand?>("Auto Chooser").apply {
            // Add the 8 ft straight as the default option in the chooser
            addDefaultOption("8 Foot Straight", AutoCommand(eightFootStraight()))

            // Add the 6x6 square path as an option
            addOption("6x6 Square", AutoCommand(squarePathTest()))

            // Add the 8 ft spin path as an option
            addOption("8 Foot Spin", AutoCommand(eightFootSpin()))

            // Add the circle path as an option
            addOption("6 foot circle", AutoCommand(circlePathTest()))
        }

    /** Chooser for test commands */
    override val testChooser: LoggedDashboardChooser<Command?> =
        LoggedDashboardChooser<Command?>("Test Chooser").apply {
            // Set up SysId routines and test command options
            addOption("Drive Translation SysId ALL", Drive.sysIDTranslationAll())
            addOption("Drive Rotation SysId ALL", Drive.sysIDRotationAll())
            addOption("Drive Steer SysId ALL", Drive.sysIDSteerAll())
            addOption("Set Angle Offsets", Drive.setAngleOffsets())
        }

    /** Autonomous commands */

    // Run the 8-foot straight path
    fun eightFootStraight(): Command {
        // Simple path used for tuning the drivetrain
        return Drive.driveAlongChoreoPath(paths["eightFootStraight"]!!, resetOdometry = true)
    }

    // Run the 6-foot square path
    fun squarePathTest(): Command {
        // Saves the full path to a variable for easy typing
        val path = paths["sixFootSquare"]!!
        return sequenceCommand(
            // Drive along the first split of the path and reset the robot position to the start of the path
            Drive.driveAlongChoreoPath(path.getSplit(0).get(), resetOdometry = true),

            runOnce { println("finished split 0") }, // Usually a robot action would be run in between splits (like scoring or picking up a game piece)

            // Drive along the second split of the path, we do not need to reset the robot position
            Drive.driveAlongChoreoPath(path.getSplit(1).get(), resetOdometry = false),

            runOnce { println("finished split 1") },

            // Continue to drive along the rest of the path
            Drive.driveAlongChoreoPath(path.getSplit(2).get(), resetOdometry = false),

            runOnce { println("finished split 2") },

            Drive.driveAlongChoreoPath(path.getSplit(3).get(), resetOdometry = false),

            runOnce { println("finished the path") },
        )
    }

    // Run the 8-foot spin path
    fun eightFootSpin(): Command {
        return Drive.driveAlongChoreoPath(paths["eightFootSpin"]!!, resetOdometry = true)
    }

    // Run the 6-foot circle path
    fun circlePathTest(): Command {
        return Drive.driveAlongChoreoPath(paths["sixFootCircle"]!!, resetOdometry = true)
    }

}