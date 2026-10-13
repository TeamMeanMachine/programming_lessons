package frc.team2471.frc2026

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.Command
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team2471.frc.lib.control.Autonomi
import org.team2471.frc.lib.control.commands.runOnceCommand


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
            addOption("Print Hello", AutoCommand(printHelloCommand())) // Add "Print Hello" auto
        }

    /** Chooser for test commands */
    override val testChooser: LoggedDashboardChooser<Command?> =
        LoggedDashboardChooser<Command?>("Test Chooser").apply {
            // Set up SysId routines and test command options
        }

    /** Autonomous commands */

    fun printHelloCommand() = runOnceCommand {
        println("hello")
    }
}