package frc.team2471.lessons

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj2.command.Command
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team2471.frc.lib.control.Autonomi
import org.team2471.frc.lib.control.commands.runOnceCommand
import org.team2471.frc.lib.control.commands.sequenceCommand
import org.team2471.frc.lib.units.seconds


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
            addOption("Elevator Auto", AutoCommand(elevatorAuto()))
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



    fun elevatorAuto() = sequenceCommand(
        Extendavator.goToPoseCmd(Pose.STOW).until { Extendavator.atSetpoint },
        Extendavator.goToPoseCmd(Pose.INTAKE_MAX).withTimeout(2.0.seconds),
        Extendavator.goToPoseCmd(Pose.STOW).until { Extendavator.atSetpoint },
        Extendavator.goToPoseCmd(Pose.SCORE).withTimeout(2.0.seconds),
        runOnceCommand {
            println("finished scoring. Going back down now")
        },
        Extendavator.goToPoseCmd(Pose.STOW).until { Extendavator.atSetpoint }
    )
}