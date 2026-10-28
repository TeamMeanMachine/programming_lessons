package frc.team2471.lessons

import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Alert
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController
import org.team2471.frc.lib.control.commands.toCommand
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.normalize
import kotlin.compareTo

object OI: SubsystemBase("OI") {
    val driverController = MeanCommandXboxController(0)

    val deadbandDriver = 0.08

    val driveTranslationX: Double
        get() = driverController.leftY.deadband(deadbandDriver)

    val driveTranslationY: Double
        get() = driverController.leftX.deadband(deadbandDriver)

    val rawDriveTranslation: Translation2d
        get() {
            val translation = Translation2d(driveTranslationX, driveTranslationY)
            return if (translation.norm > 1.0) {
                translation.normalize()
            } else {
                translation
            }
        }

    val driveRotation: Double
        get() = -driverController.rightX.deadband(deadbandDriver)

    init {
        println("inside OI init")
        // Place OI Joystick code triggers here

        // Default command, normal field-relative drive
        Drive.defaultCommand = Drive.joystickPercentageDrive()

        // Switch to X pattern when X button is pressed
        driverController.x().onTrue(Commands.runOnce({ Drive.xPose() }, Drive))

        // Reset gyro to 0° or 180° when back button is pressed
        driverController.back().onTrue({
            println("zero gyro")
            Drive.zeroGyro()
        }.toCommand(Drive).ignoringDisable(true))

        println("end of OI init")
    }
}