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

        //TODO: Use left and right triggers to run percent output



        //TODO: up Dpad -> move up 1 inch


        //TODO: down Dpad -> move down 1 inch





        //TODO: use motionMagic to move up/down 10 inches smoothly using left/right dPad






        println("end of OI init")
    }
}