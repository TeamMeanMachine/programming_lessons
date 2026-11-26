package frc.team2471.lessons

import com.ctre.phoenix6.swerve.utility.PhoenixPIDController
import org.team2471.frc.lib.commands.command
import org.team2471.frc.lib.environment.demoSpeed
import org.team2471.frc.lib.environment.isBlueAlliance
import org.team2471.frc.lib.logging.LoopLogger
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.square
import org.team2471.frc.lib.swerve.SwerveDriveSubsystem
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.inches
import org.team2471.frc.lib.math.normalize
import org.team2471.frc.lib.units.asMetersPerSecondSquared
import org.team2471.frc.lib.units.inchesPerSecond
import org.team2471.frc.lib.units.metersPerSecondSquared
import org.team2471.frc.lib.units.perSecond
import org.wpilib.math.controller.PIDController
import org.wpilib.math.geometry.Pose2d
import org.wpilib.math.geometry.Rotation2d
import org.wpilib.math.geometry.Translation2d
import org.wpilib.math.kinematics.ChassisVelocities
import org.wpilib.math.linalg.Matrix
import org.wpilib.math.linalg.VecBuilder
import org.wpilib.math.numbers.N1
import org.wpilib.math.numbers.N3


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
    override val autoPilot = createAPObject(Double.POSITIVE_INFINITY.inchesPerSecond, 100.0.metersPerSecondSquared, 2.0.metersPerSecondSquared.perSecond, 0.5.inches, 1.0.degrees)

    override val pathTranslationController = PIDController(7.0, 0.0, 0.0)
    override val pathThetaController = PIDController(8.0, 0.0, 0.0)

    override val autoDriveToPointController = PIDController(3.0, 0.0, 0.1)
    override val teleopDriveToPointController = PIDController(3.0, 0.0, 0.1)

    override val driveAtAnglePIDController = PhoenixPIDController(7.7, 0.0, 0.072)

    /** false = paths made on the blue side, true = paths made on the red side */
    override val choreoPathsStartOnRed: Boolean = false

    override var centerOfRotation: Translation2d = Translation2d(0.0.inches, 0.0.inches)

    init {
        println("Drive initialization")

        useMapleSim = false

        // MUST start inside the field on bootup for accurate heading measurements due to a Particle Filter bug.
        pose = Pose2d(3.0, 3.0, heading)

        println("max acceleration ${DriveConstants.kMaxAcceleration.asMetersPerSecondSquared}")
    }

    override fun periodic() {
        LoopLogger.record("Drive periodic")

        LoopLogger.record("super Drive periodic")
        super.periodic()
        LoopLogger.record("super Drive periodic")

        LoopLogger.record("Drive periodic")
    }


    override fun defaultCommand() = command("Drive Default", this) {
        await(joystickPercentageDrive())
    }

    /**
     * Returns [ChassisVelocities] with a percentage power from the driver controller.
     */
    override fun getJoystickPercentageVelocities(): ChassisVelocities {
        val rawJoystick = OI.driveTranslation
        // Square drive input and apply demoSpeed
        val power = rawJoystick.norm.square() * demoSpeed
        // Apply modified power to joystick vector and flip depending on alliance
        val joystickTranslation = rawJoystick.normalize() * power * if (isBlueAlliance) -1.0 else 1.0

        // Cube rotation input and apply demoSpeed
        val omega = OI.driveRotation.cube() * demoSpeed

        return ChassisVelocities(joystickTranslation.x, joystickTranslation.y, omega)
    }

    /** Command to zero robot gyro */
    fun zeroGyroCommand() = command("zeroGyroCommand", Drive) {
        println("zero gyro command")
        zeroGyro()
    }

}