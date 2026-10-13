package frc.team2471.frc2026

import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController

object OI: SubsystemBase("OI") {

    // Controller
    val driverController = MeanCommandXboxController(0)

    init {
        println("inside OI init")
        // Place OI Joystick code triggers here


        //TODO: step 4: when driver presses a button set Intake power to 0.9
        //TODO: step 4: when driver presses b button set Intake power to 0.0


        //TODO: step 6: Use driver left and right triggers to set any power between -1.0 to 1.0. Set to 0.0 if not pressing triggers.






        println("end of OI init")
    }
}