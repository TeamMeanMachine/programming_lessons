package frc.team2471.lessons

import com.ctre.phoenix6.controls.PositionVoltage
import org.littletonrobotics.junction.AutoLogOutput
import org.team2471.frc.lib.commands.MechanismBase
import org.team2471.frc.lib.commands.command
import org.team2471.frc.lib.hardware.ctre.*
import org.team2471.frc.lib.hardware.loggedMotors.LoggedTalonFX
import org.team2471.frc.lib.units.*
import org.wpilib.math.geometry.Pose3d
import org.wpilib.math.geometry.Rotation3d
import org.wpilib.math.geometry.Translation3d
import org.wpilib.math.system.DCMotor
import org.wpilib.units.measure.Angle
import org.wpilib.units.measure.Distance

object Extendavator: MechanismBase("Extendavator") {

    // LoggedTalonFX is the same as normal TalonFX, except it supports simulation, logging, and replay.
    val elevatorMotor = LoggedTalonFX(Talons.ELEVATOR_0)
    val extensionMotor = LoggedTalonFX(Talons.EXTENSION)
    val pivotMotor = LoggedTalonFX(Talons.INTAKE_PIVOT)

    val MIN_EXTENSION_DIST = 0.0.inches
    val MAX_EXTENSION_DIST = 14.0.inches

    val MIN_ELEVATOR_HEIGHT = 0.0.inches
    val MAX_ELEVATOR_HEIGHT = 59.25.inches

    val MIN_PIVOT_ANGLE = -90.0.degrees
    val MAX_PIVOT_ANGLE = 90.0.degrees

    @get:AutoLogOutput(key = "Extendavator/elevatorSetpoint")
    var elevatorSetpoint = 0.0.feet
        set(value) {
            val safeValue = value.coerceIn(MIN_ELEVATOR_HEIGHT, MAX_ELEVATOR_HEIGHT)
            elevatorMotor.setControl(PositionVoltage(safeValue.asInches))
            field = safeValue
        }

    @get:AutoLogOutput(key = "Extendavator/extensionSetpoint")
    var extensionSetpoint = MIN_EXTENSION_DIST
        set(value) {
            val safeValue = value.coerceIn(MIN_EXTENSION_DIST, MAX_EXTENSION_DIST)
            extensionMotor.setControl(PositionVoltage(safeValue.asInches))
            field = safeValue
        }

    @get:AutoLogOutput(key = "Extendavator/pivotSetpoint")
    var pivotSetpoint = 0.0.degrees
        set(value) {
            val safeValue = value.coerceIn(MIN_PIVOT_ANGLE, MAX_PIVOT_ANGLE)
            pivotMotor.setControl(PositionVoltage(safeValue))
            field = safeValue
        }

    @get:AutoLogOutput(key = "Extendavator/elevatorHeight")
    val elevatorHeight: Distance get() = elevatorMotor.position.valueAsDouble.inches
    @get:AutoLogOutput(key = "Extendavator/extensionDistance")
    val extensionDistance: Distance get() = extensionMotor.position.valueAsDouble.inches
    @get:AutoLogOutput(key = "Extendavator/pivotAngle")
    val pivotAngle: Angle get() = pivotMotor.position.value

    @get:AutoLogOutput(key = "Extendavator/heightError")
    val heightError: Distance get() = elevatorMotor.closedLoopError.valueAsDouble.inches
    @get:AutoLogOutput(key = "Extendavator/extensionError")
    val extensionError: Distance get() = extensionMotor.closedLoopError.valueAsDouble.inches
    @get:AutoLogOutput(key = "Extendavator/pivotError")
    val pivotError: Angle get() = pivotMotor.closedLoopError.valueAsDouble.rotations

    val totalErrorPose: Pose get() = Pose(heightError, extensionError, pivotError)

    @get:AutoLogOutput(key = "Extendavator/atSetpoint")
    val atSetpoint: Boolean
        get() {
            val error = totalErrorPose.absoluteValue()
            return error.pivotAngle < 1.0.degrees && error.elevatorHeight < 1.0.inches && error.extensionDistance < 1.0.inches
        }

    init {

        elevatorMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(1.0/5.33) // Gear ratio, motor rotations to inches height
            p(0.2)
        }
        elevatorMotor.addFollower(Talons.ELEVATOR_1)
        elevatorMotor.configSim(DCMotor.getKrakenX60(2), 0.0001)

        extensionMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(1.0/1.05) // Gear ratio, motor rotations to inches extension
            p(0.5)
        }
        extensionMotor.configSim(DCMotor.getKrakenX60(1), 0.0001)

        pivotMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(33.75) // Gear ratio, motor rotations to pivot rotations
            p(17.0)
        }
        pivotMotor.configSim(DCMotor.getKrakenX60(1), 0.0001)
    }

    fun goToPoseCmd(pose: Pose) = command("goToPose", this) {
        elevatorSetpoint = pose.elevatorHeight
        extensionSetpoint = pose.extensionDistance
        pivotSetpoint = pose.pivotAngle
    }






    @get:AutoLogOutput(key = "Components/measuredComponents")
    val measuredComponents: Array<Pose3d> // Simulated motor components of extendavator
        get() {
            return createComponentArray(
                0.0.degrees,
                Extendavator.elevatorHeight,
                Extendavator.extensionDistance,
                Extendavator.pivotAngle
            )
        }

    @get:AutoLogOutput(key = "Components/setpointComponents")
    val setpointComponents: Array<Pose3d> // Setpoint components of extendavator
        get() {
            return createComponentArray(
                0.0.degrees,
                Extendavator.elevatorSetpoint,
                Extendavator.extensionSetpoint,
                Extendavator.pivotSetpoint
            )
        }

    // Creates an array of Pose3d representing the components of the extendavator. For AdvantageScope visualization
    private fun createComponentArray(turretAngle: Angle, carriageHeight: Distance, extensionDistance: Distance, wristAngle: Angle): Array<Pose3d> {
        val turretRotation = Rotation3d(0.0.degrees, 0.0.degrees, turretAngle)
        val wristRotation = Rotation3d(0.0.degrees, wristAngle, turretAngle)

        val wristComponentOffset = Translation3d(0.31, 0.0, 0.445)

        val extensionTranslation = Translation3d(extensionDistance * cos(turretAngle), extensionDistance * sin(turretAngle), carriageHeight)
        val wristExtensionDistance = extensionDistance + wristComponentOffset.x.meters
        val wristTranslation = Translation3d(wristExtensionDistance * cos(turretAngle), wristExtensionDistance * sin(turretAngle), carriageHeight + wristComponentOffset.z.meters)

        return arrayOf(
            Pose3d(Translation3d(), turretRotation),
            Pose3d(Translation3d(0.0, 0.0, carriageHeight.asMeters), turretRotation),
            Pose3d(extensionTranslation, turretRotation),
            Pose3d(wristTranslation, wristRotation)
        )
    }
}