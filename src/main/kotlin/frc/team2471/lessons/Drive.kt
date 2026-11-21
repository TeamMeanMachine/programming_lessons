package frc.team2471.lessons

import com.ctre.phoenix6.swerve.utility.PhoenixPIDController
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import org.team2471.frc.lib.control.LoopLogger
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.square
import org.team2471.frc.lib.swerve.SwerveDriveSubsystem
import org.team2471.frc.lib.units.asMetersPerSecondPerSecond
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.inches
import org.team2471.frc.lib.math.normalize
import org.team2471.frc.lib.units.inchesPerSecond
import org.team2471.frc.lib.units.metersPerSecondPerSecond
import org.team2471.frc.lib.units.perSecond
import org.team2471.frc.lib.util.demoSpeed
import org.team2471.frc.lib.util.isBlueAlliance


object Drive: SwerveDriveSubsystem(DriveConstants.drivetrainConstants, *DriveConstants.moduleConfigs) {

    // To reset position use this, also add other pose sources that need reset here.
    override var pose: Pose2d
        get() = savedState.Pose
        set(value) {
            resetPose(value)
        }

    override var heading: Rotation2d
        get() = pose.rotation
        set(value) {
//            println("resting heading to ${value.degrees}")
            resetRotation(value)
        }

    val DRIVE_STD_DEVS: Matrix<N3?, N1?> = VecBuilder.fill(0.1, 0.1, 0.05)

    // Drive Feedback controllers
    override val autoPilot = createAPObject(Double.POSITIVE_INFINITY.inchesPerSecond, 100.0.metersPerSecondPerSecond, 2.0.metersPerSecondPerSecond.perSecond, 0.5.inches, 1.0.degrees)

    override val pathXController = PIDController(7.0, 0.0, 0.0)
    override val pathYController = PIDController(7.0, 0.0, 0.0)
    override val pathThetaController = PIDController(8.0, 0.0, 0.0)

    override val autoDriveToPointController = PIDController(3.0, 0.0, 0.1)
    override val teleopDriveToPointController = PIDController(3.0, 0.0, 0.1)

    override val driveAtAnglePIDController = PhoenixPIDController(7.7, 0.0, 0.072)

    override val isDisabledSupplier: () -> Boolean = { Robot.isDisabled }

    override val choreoPathsStartOnRed: Boolean = false // false=made on the blue side, true=made on the red side

    init {
        println("inside Drive init")

        useMapleSim = true

        setStateStdDevs(DRIVE_STD_DEVS)

        println("max acceleration ${DriveConstants.kMaxAcceleration.asMetersPerSecondPerSecond}")

        finalInitialization()
    }

    override fun periodic() {
        LoopLogger.record("Inside Drive periodic")

        LoopLogger.record("b4 Drive piodc")
        super.periodic() // Must call this
        LoopLogger.record("super Drive piodc")

        LoopLogger.record("Drive pirdc")
    }

    /**
     * Returns [ChassisSpeeds] with a percentage power from the driver controller.
     */
    override fun getJoystickPercentageSpeeds(): ChassisSpeeds {
        val rawJoystick = OI.rawDriveTranslation
        // Square drive input and apply demoSpeed
        val power = rawJoystick.norm.square() * demoSpeed
        // Apply modified power to joystick vector and flip depending on alliance
        val joystickTranslation = rawJoystick.normalize() * power * if (isBlueAlliance) -1.0 else 1.0

        // Cube rotation input and apply demoSpeed
        val omega = OI.driveRotation.cube() * demoSpeed

        return ChassisSpeeds(joystickTranslation.x, joystickTranslation.y, omega)
    }

}