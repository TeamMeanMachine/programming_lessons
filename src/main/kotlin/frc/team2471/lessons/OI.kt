package frc.team2471.lessons

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.control.MeanCommandXboxController
import org.team2471.frc.lib.control.commands.toCommand
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.normalize
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.feet

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
        Extendavator.defaultCommand = Extendavator.goToPoseCmd(Pose.STOW)

        driverController.x().whileTrue(Extendavator.goToPoseCmd(Pose.INTAKE_MIN))
        driverController.b().whileTrue(Extendavator.goToPoseCmd(Pose.INTAKE_MAX))
        driverController.a().whileTrue(Extendavator.goToPoseCmd(Pose.SCORE))
        driverController.y().whileTrue(Extendavator.goToPoseCmd(Pose.SCORE + Pose(1.75.feet, 3.0.feet, 90.0.degrees)))

        driverController.rightTrigger(0.1).whileTrue(Extendavator.moveBetweenPosesCmd())
        driverController.leftTrigger(0.1).whileTrue(Extendavator.controlWithJoystick())

        // Reset gyro to 0° or 180° when back button is pressed
        driverController.back().onTrue({
            println("zero gyro")
            Drive.zeroGyro()
        }.toCommand(Drive).ignoringDisable(true))

        println("end of OI init")
    }
}