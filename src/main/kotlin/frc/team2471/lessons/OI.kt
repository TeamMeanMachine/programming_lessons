package frc.team2471.lessons

import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController
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

        println("end of OI init")
    }
}