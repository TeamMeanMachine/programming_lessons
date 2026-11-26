package frc.team2471.lessons

import com.ctre.phoenix6.controls.PositionVoltage
import org.littletonrobotics.junction.AutoLogOutput
import org.team2471.frc.lib.commands.MechanismBase
import org.team2471.frc.lib.commands.command
import org.team2471.frc.lib.commands.periodic
import org.team2471.frc.lib.hardware.ctre.*
import org.team2471.frc.lib.hardware.loggedMotors.LoggedTalonFX
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.units.*
import org.wpilib.math.geometry.Pose3d
import org.wpilib.math.geometry.Rotation3d
import org.wpilib.math.geometry.Translation3d
import org.wpilib.math.system.DCMotor
import org.wpilib.telemetry.Telemetry
import org.wpilib.units.measure.Angle
import org.wpilib.units.measure.Distance

object Extendavator: MechanismBase("Extendavator") {
    val table = Telemetry.getTable("Extendavator")

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
            p(0.5)
        }
        extensionMotor.configSim(DCMotor.getKrakenX44(1), 0.0001)

        pivotMotor.applyConfiguration {
            currentLimits(30.0, 40.0, 1.0)
            sensorToMechanismRatio(33.75)
            p(17.0)
        }
        pivotMotor.configSim(DCMotor.getKrakenX44(1), 0.0001)
    }

    fun goToPose(pose: Pose) {
        elevatorSetpoint = pose.elevatorHeight
        extensionSetpoint = pose.extensionDistance
        pivotSetpoint = pose.pivotAngle
    }

    fun goToPoseCmd(pose: Pose, stopWhenAtSetpoint: Boolean = true) = command("goToPoseCmd", this) {
        periodic {
            goToPose(pose)
            val error = (pose - Pose.current).absoluteValue()

            if (stopWhenAtSetpoint && error.elevatorHeight < 1.0.inches && error.extensionDistance < 1.0.inches && error.pivotAngle < 1.0.degrees) {
                stop()
                println("finished going to pose")
            }
        }
    }

    fun zeroExtension() {
        extensionMotor.setPosition(MIN_EXTENSION_DIST.asInches)
    }

    fun moveBetweenPosesCmd() = command("moveBetweenPosesCmd",this) {
        periodic {
            val pose1 = Pose.INTAKE_MIN
            val pose2 = Pose.INTAKE_MAX
            val poseDifference = pose2 - pose1
            val interpolatedPose = pose1 + poseDifference * OI.driverController.rightTrigger.deadband(0.1)

            goToPose(interpolatedPose)
        }
    }

    fun controlWithJoystick() = command("controlWithJoystick", this, Drive) { // Require Extendavator and Drive because we are using both Mechanisms.
        periodic {
            val height =  MAX_ELEVATOR_HEIGHT * -OI.driverController.leftY.coerceIn(-1.0, 0.0)
            val extension = MAX_EXTENSION_DIST * OI.driverController.leftX.coerceIn(0.0, 1.0)
            val pivot = MAX_PIVOT_ANGLE * OI.driverController.rightY

            val pose = Pose(height, extension, pivot)

            goToPose(pose) // Tell Extendavator to move to the pose
            Drive.stop() // Stop moving the drivetrain because we are using the joysticks
        }
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