package frc.team2471.lessons

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.Command
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team2471.frc.lib.control.Autonomi
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
            addOption("Print Hello", AutoCommand(printHelloCommand()))

            addOption("8 Foot Straight", AutoCommand(eightFootStraight()))

            addOption("Square Path Auto", AutoCommand(squarePathAuto()))

            addOption("8 Foot Spin", AutoCommand(eightFootSpin()))

            addOption("Circle Path Auto", AutoCommand(circlePathAuto()))
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

    fun printHelloCommand(): Command {
        return runOnceCommand { println("Hello, world!") }
    }

    fun eightFootStraight(): Command {
        return Drive.driveAlongChoreoPath(paths["eightFootStraight"]!!, resetOdometry = true)
    }

    fun squarePathAuto(): Command {
        val path = paths["sixFootSquare"]!!
        return sequenceCommand(
            Drive.driveAlongChoreoPath(path.getSplit(0).get(), resetOdometry = true),

            runOnceCommand { println("finished split 0") },

            Drive.driveAlongChoreoPath(path.getSplit(1).get(), resetOdometry = false),

            runOnceCommand { println("finished split 1") },

            Drive.driveAlongChoreoPath(path.getSplit(2).get(), resetOdometry = false),

            runOnceCommand { println("finished split 2") },

            Drive.driveAlongChoreoPath(path.getSplit(3).get(), resetOdometry = false),

            runOnceCommand { println("finished the path") },
        )
    }

    // Run the 8-foot spin path
    fun eightFootSpin(): Command {
        return Drive.driveAlongChoreoPath(paths["eightFootSpin"]!!, resetOdometry = true)
    }

    // Run the 6-foot circle path
    fun circlePathAuto(): Command {
        return Drive.driveAlongChoreoPath(paths["sixFootCircle"]!!, resetOdometry = true)
    }

}