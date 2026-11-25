package frc.team2471.lessons

import com.ctre.phoenix6.controls.PositionVoltage
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.littletonrobotics.junction.AutoLogOutput
import org.team2471.frc.lib.control.commands.runCommand
import org.team2471.frc.lib.hardware.ctre.addFollower
import org.team2471.frc.lib.hardware.ctre.applyConfiguration
import org.team2471.frc.lib.hardware.ctre.currentLimits
import org.team2471.frc.lib.hardware.ctre.p
import org.team2471.frc.lib.hardware.ctre.sensorToMechanismRatio
import org.team2471.frc.lib.hardware.loggedMotors.LoggedTalonFX
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.units.asInches
import org.team2471.frc.lib.units.asMeters
import org.team2471.frc.lib.units.cos
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.feet
import org.team2471.frc.lib.units.inches
import org.team2471.frc.lib.units.meters
import org.team2471.frc.lib.units.rotations
import org.team2471.frc.lib.units.sin

object Extendavator: SubsystemBase("Extendavator") {
    val elevatorMotor = LoggedTalonFX(Talons.ELEVATOR_0)
    val extensionMotor = LoggedTalonFX(Talons.EXTENSION)
    val pivotMotor = LoggedTalonFX(Talons.INTAKE_PIVOT)

    val MIN_EXTENSION_DIST = 22.0.inches
    val MAX_EXTENSION_DIST = 3.5.feet

    val MAX_ELEVATOR_HEIGHT = 59.25.inches
    val MAX_PIVOT_ANGLE = 90.0.degrees

    @get:AutoLogOutput(key = "Extendavator/elevatorSetpoint")
    var elevatorSetpoint = 0.0.feet
        set(value) {
            val safeValue = value.coerceIn(0.0.inches, MAX_ELEVATOR_HEIGHT)
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
            val safeValue = value.coerceIn(-MAX_PIVOT_ANGLE, MAX_PIVOT_ANGLE)
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
        zeroExtension()

        elevatorMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(1.0/5.33)
            p(0.2)
        }
        elevatorMotor.addFollower(Talons.ELEVATOR_1)
        elevatorMotor.configSim(DCMotor.getKrakenX60(2), 0.0001)

        extensionMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            //        Motor to sprocket ratio * 12 tooth sprocket * 0.25in chain link length
            sensorToMechanismRatio(1.0/1.05)
            p(0.4)
        }
        extensionMotor.configSim(DCMotor.getKrakenX44(1), 0.0000000000001)

        pivotMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(33.75)
            p(17.0)
        }
        pivotMotor.configSim(DCMotor.getKrakenX44(1), 0.000000000001)
    }

    fun goToPose(pose: Pose) {
        elevatorSetpoint = pose.elevatorHeight
        extensionSetpoint = pose.extensionDistance
        pivotSetpoint = pose.pivotAngle
    }

    fun goToPoseCmd(pose: Pose): Command = runCommand(this) {
        goToPose(pose)
    }

    fun zeroExtension() {
        extensionMotor.setPosition(MIN_EXTENSION_DIST.asInches)
    }


    fun moveBetweenPosesCmd() = runCommand(this) {
        val pose1 = Pose.INTAKE_MIN
        val pose2 = Pose.INTAKE_MAX
        val poseDifference = pose2 - pose1
        val interpolatedPose = pose1 + poseDifference * OI.driverController.rightTriggerAxis.deadband(0.1)

        goToPose(interpolatedPose)
    }

    fun controlWithJoystick() = runCommand(this, Drive) {
        val height =  MAX_ELEVATOR_HEIGHT * -OI.driverController.leftY.coerceIn(-1.0, 0.0)
        val extension = MAX_EXTENSION_DIST * OI.driverController.leftX.coerceIn(0.0, 1.0)
        val pivot = MAX_PIVOT_ANGLE * OI.driverController.rightY

        val pose = Pose(height, extension, pivot)

        goToPose(pose) // Tell Extendavator to move to the pose
        Drive.stop() // Stop moving the drivetrain because we are using the joysticks
    }







    @get:AutoLogOutput(key = "Components/measuredComponents")
    val measuredComponents: Array<Pose3d>
        get() {
            return createComponentArray(
                0.0.degrees,
                Extendavator.elevatorHeight,
                Extendavator.extensionDistance - Extendavator.MIN_EXTENSION_DIST,
                Extendavator.pivotAngle
            )
        }

    @get:AutoLogOutput(key = "Components/setpointComponents")
    val setpointComponents: Array<Pose3d>
        get() {
            return createComponentArray(
                0.0.degrees,
                Extendavator.elevatorSetpoint,
                Extendavator.extensionSetpoint - Extendavator.MIN_EXTENSION_DIST,
                Extendavator.pivotSetpoint
            )
        }

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