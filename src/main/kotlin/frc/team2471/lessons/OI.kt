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
        driverController.rightTrigger(0.1).whileTrue(
            runCommand {
                Shooter.shooterVelocitySetpoint = (driverController.rightTriggerAxis * Shooter.MAX_VELOCITY)
            }.finallyRun {
                Shooter.stop()
            }
        )

        // TODO: map a to 0 RPM velocity, b to 1250 RPM, and y to 3000 RPM
        driverController.a().whileTrue(runCommand {
            Shooter.shooterVelocitySetpoint = 0.0
        })

        driverController.b().whileTrue(runCommand {
            Shooter.shooterVelocitySetpoint = 1250.0
        })

        driverController.y().whileTrue(runCommand {
            Shooter.shooterVelocitySetpoint = 3000.0
        })




        println("end of OI init")
    }
}