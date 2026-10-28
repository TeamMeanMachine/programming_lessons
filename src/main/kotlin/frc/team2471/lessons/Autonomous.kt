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

            // TODO: Add more auto commands to the chooser here...


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
        return sequenceCommand(
            runOnceCommand { println("Hello I am an autonomous function!") },
            runOnceCommand { println("I am the second command in the sequence!") },
            runOnceCommand { println("Ok bye!") }
        )
    }

    // TODO: Create custom driveAlongChoreoPath auto routines here






}