package frc.team2471.lessons

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

        // TODO: map the right trigger to setting the open loop voltage of the shooter motor.  full trigger should be MAX_VELOCITY








        // TODO: map a to 0 RPM velocity, b to 1250 RPM, and y to 3000 RPM















        println("end of OI init")
    }
}