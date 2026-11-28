package frc.team2471.lessons

import org.team2471.frc.lib.commands.MechanismBase
import org.team2471.frc.lib.commands.command
import org.team2471.frc.lib.logging.getTunable
import org.team2471.frc.lib.math.applyDeadband
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.normalize
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.feet
import org.wpilib.command3.button.CommandXboxController
import org.wpilib.math.geometry.Pose2d
import org.wpilib.math.geometry.Translation2d
import org.wpilib.opmode.PeriodicOpMode
import org.wpilib.opmode.Teleop
import org.wpilib.telemetry.Telemetry

object OI: MechanismBase("OI") {
    private val table = Telemetry.getTable("OI")

    val rotationMultiplierEntry = table.getTunable("Rotation Multiplier", 0.8, true)
    val rotationMultiplier: Double
        get() = rotationMultiplierEntry.get()

    val driverController = CommandXboxController(0)
    val operatorController = CommandXboxController(1)

    val deadbandDriver = 0.08
    val deadbandOperator = 0.1

    val rawDriveTranslationX: Double
        get() = driverController.leftY

    val rawDriveTranslationY: Double
        get() = driverController.leftX

    val driveTranslation: Translation2d
        get() {
            val rawTranslation = Translation2d(rawDriveTranslationX, rawDriveTranslationY)
            return if (rawTranslation.norm > 1.0) {
                rawTranslation.normalize().applyDeadband(deadbandDriver)
            } else {
                rawTranslation.applyDeadband(deadbandDriver)
            }
        }

    val driveTranslationX: Double get() = driveTranslation.x
    val driveTranslationY: Double get() = driveTranslation.y

    val driveRotation: Double
        get() = -driverController.rightX.deadband(deadbandDriver) * rotationMultiplier

    val driveLeftTrigger: Double
        get() = driverController.leftTrigger

    val driveLeftTriggerFullPress: Boolean
        get() = driverController.leftTrigger > 0.95

    val driveRightTrigger: Double
        get() = driverController.rightTrigger

    val driveRightTriggerFullPress: Boolean
        get() = driverController.rightTrigger > 0.95

    val operatorLeftTrigger: Double
        get() = operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftY.deadband(deadbandOperator)

    val operatorLeftX: Double
        get() = operatorController.leftX.deadband(deadbandOperator)

    val operatorRightTrigger: Double
        get() = operatorController.rightTrigger

    val operatorRightX: Double
        get() = operatorController.rightX.deadband(deadbandOperator)

    val operatorRightY: Double
        get() = operatorController.rightY.deadband(deadbandOperator)

    init {
        println("OI initialization")

        /** DEFAULT JOYSTICK BINDINGS. (Will be active by default in every OpMode unless overridden) */

        // TODO: Bind a, b, x, y to different Poses from Pose.kt using goToPoseUntilAtSetpointCmd()






        driverController.menu().onTrue(Drive.zeroGyroCommand()) // Zero Gyro
        driverController.view().onTrue(command("zero pose") { Drive.pose = Pose2d(Translation2d(3.0, 3.0), Drive.heading) }) // Reset Odometry Position
    }



    // Teleop OpMode. Supports scoping. Can bind specific triggers in teleop only if desired.
    @Teleop(name = "Country Roads!")
    class TeleopMode: PeriodicOpMode() {
        init { println("TeleopMode selected") }
        override fun disabledPeriodic() {}
        override fun start() { println("Teleop Mode start!") }
        override fun periodic() {}
        override fun end() { println("Teleop Mode end!") }
    }
}