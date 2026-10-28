package frc.team2471.lessons

import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController
import org.team2471.frc.lib.control.commands.finallyRun
import org.team2471.frc.lib.control.commands.runCommand

object OI: SubsystemBase("OI") {

    // Controller
    val driverController = MeanCommandXboxController(0)

    init {
        println("inside OI init")
        // Place OI Joystick code triggers here


        //When driver presses a button set Intake power to 0.9
        driverController.a().onTrue(
            Commands.runOnce({IntakeSubsystem.setVoltage(8.0)})
        )

        //When driver presses b button set Intake power to 0.0
        driverController.b().onTrue(
            Commands.runOnce({IntakeSubsystem.setVoltage(0.0)})
        )

        //Use driver left and right triggers to set any power between -1.0 to 1.0. Set to 0.0 if not pressing triggers.
        driverController.leftTrigger(0.1).or(driverController.rightTrigger(0.1)).whileTrue(
            runCommand {
                IntakeSubsystem.setVoltage( (driverController.leftTriggerAxis - driverController.rightTriggerAxis) * 12.0 )
            }.finallyRun {
                IntakeSubsystem.setVoltage(0.0)
            }
        )

        //percent output triggers
//        driverController.leftTrigger(0.1).or(driverController.rightTrigger(0.1)).whileTrue(
//            runCommand {
//                IntakeSubsystem.setPower( driverController.leftTriggerAxis - driverController.rightTriggerAxis )
//            }.finallyRun {
//                IntakeSubsystem.setVoltage(0.0)
//            }
//        )

        println("end of OI init")
    }
}