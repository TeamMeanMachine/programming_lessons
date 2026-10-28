package frc.team2471.lessons

import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController
import org.team2471.frc.lib.control.commands.finallyRun
import org.team2471.frc.lib.control.commands.runCommand
import org.team2471.frc.lib.control.commands.runOnceCommand
import org.team2471.frc.lib.units.degrees

object OI: SubsystemBase("OI") {

    // Controller
    val driverController = MeanCommandXboxController(0)

    init {
        println("inside OI init")
        // Place OI Joystick code triggers here

        driverController.povUp().onTrue(
            runOnceCommand {
                println("going positive ${Elevator.motorAngle}")
                Elevator.setMotionMagic(Elevator.motorAngle + 90.0.degrees)
            }
        )

        driverController.povDown().onTrue(
            runOnceCommand {
                println("going negative ${Elevator.motorAngle}")
                Elevator.setMotionMagic(Elevator.motorAngle - 90.0.degrees)
            }
        )

        driverController.leftTrigger(0.1).or(driverController.rightTrigger(0.1)).whileTrue(
            runCommand {
                Elevator.setPercentOut( driverController.leftTriggerAxis - driverController.rightTriggerAxis )
            }.finallyRun {
                Elevator.setPercentOut(0.0)
            }
        )

        println("end of OI init")
    }
}