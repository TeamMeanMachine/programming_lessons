@file:JvmName("Main") // set the compiled Java class name to "Main" rather than "MainKt"
package frc.team2471.lessons

import edu.wpi.first.hal.FRCNetComm.tInstances
import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.Commands
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.LoggedRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team2471.frc.lib.control.LoopLogger
import org.team2471.frc.lib.hardware.loggedMotors.MasterMotor
import org.team2471.frc.lib.util.RobotMode
import org.team2471.frc.lib.util.robotMode


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
object Robot : LoggedRobot() {
    private var wasDisabled = true
    var beforeFirstEnable = true
        private set

    val commandScheduler = CommandScheduler.getInstance()

    @get:JvmName("RobotIsEnabled")
    val isEnabled: Boolean
        get() = DriverStation.isEnabled()

    override fun isEnabled(): Boolean = DriverStation.isEnabled()
    override fun isAutonomous(): Boolean = DriverStation.isAutonomous()
    override fun isDisabled(): Boolean = DriverStation.isDisabled()
    override fun isAutonomousEnabled(): Boolean = DriverStation.isAutonomousEnabled()

    // Subsystems:
    // MUST define an individual variable for all subsystems inside this class or else @AutoLogOutput will not work -2025
    val oi = OI
    val elevator = Elevator

    var allSubsystems = arrayOf(elevator, oi)

    init {
        // Tells FRC we use Kotlin
        HAL.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Kotlin)

        // Set up data receivers & replay source
        when (robotMode) {
            RobotMode.REAL -> { // Running on a real robot, log to a USB stick ("/U/logs")
//                Logger.addDataReceiver(WPILOGWriter())
                Logger.addDataReceiver(NT4Publisher()) // Only log to NT if FMS is not connected
            }
            RobotMode.SIM -> {
                Logger.addDataReceiver(NT4Publisher())
                Logger.addDataReceiver(WPILOGWriter())
            } // Running a physics simulator, log to NT
            RobotMode.REPLAY -> { // Replaying a log, set up replay source
                setUseTiming(true) // false - simulate as fast as possible, true - simulate in real time (particle filter needs true)
                val logPath = LogFileUtil.findReplayLog()
                Logger.setReplaySource(WPILOGReader(logPath))
                Logger.addDataReceiver(WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim")))
            }
        }

        DriverStation.silenceJoystickConnectionWarning(true)

        // Start AdvantageKit logger
        Logger.start()
        // Call all subsystems, make sure their init's run
        allSubsystems.forEach { println("activating subsystem ${it.name}") }
        println("We see ${Autonomous.paths.size} paths.")
    }

    /** This function is called periodically during all modes.  */
    override fun robotPeriodic() {
        LoopLogger.reset()
        LoopLogger.record("after LL reset")

        if (isEnabled) {
            if (wasDisabled) {
                enabledInit()
                beforeFirstEnable = false
                wasDisabled = false
            }
        } else {
            wasDisabled = true
        }

        LoopLogger.record("b4 CommandScheduler")
        try {
            commandScheduler.run()
        } catch (e: ConcurrentModificationException) {
            println("ConcurrentModificationException!!!! $e")
        }
        LoopLogger.record("after CommandScheduler")
        MasterMotor.periodic()
        LoopLogger.record("Robot periodic()")
    }

    fun enabledInit() {}

    /** This function is called once when the robot is disabled.  */
    override fun disabledInit() {
        Autonomous.autonomousCommand?.cancel() // This makes sure that the autonomous stops running when teleop starts running.
        Autonomous.testCommand?.cancel()
    }

    /** This function is called periodically when disabled.  */
    override fun disabledPeriodic() {
        if (beforeFirstEnable) {
            Autonomous.updateSelectedAuto(true)
        } else {
            Autonomous.updateSelectedAuto(false)
        }
    }

    /** This function is called once when auto is enabled.  */
    override fun autonomousInit() {
        commandScheduler.schedule(Autonomous.autonomousCommand ?: Commands.runOnce({ println("THE AUTONOMOUS COMMAND IS NULL") }))
    }

    /** This function is called periodically during autonomous.  */
    override fun autonomousPeriodic() {}

    /** This function is called once when teleop is enabled.  */
    override fun teleopInit() {}

    /** This function is called periodically during operator control.  */
    override fun teleopPeriodic() {}

    /** This function is called once when test mode is enabled.  */
    override fun testInit() {
        CommandScheduler.getInstance().cancelAll() // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().schedule((Autonomous.testCommand ?: Commands.runOnce({println("THE TEST COMMAND IS NULL")})))
    }

    /** This function is called periodically during test mode.  */
    override fun testPeriodic() {}

    /** This function is called once when the robot is first started up.  */
    override fun simulationInit() {}

    /** This function is called periodically whilst in simulation.  */
    override fun simulationPeriodic() {}
}

/**
 * Main initialization function. Do not perform any initialization here
 * other than calling `RobotBase.startRobot`. Do not modify this file
 * except to change the object passed to the `startRobot` call.
 *
 * If you change the package of this file, you must also update the
 * `ROBOT_MAIN_CLASS` variable in the gradle build file. Note that
 * this file has a `@file:JvmName` annotation so that its compiled
 * Java class name is "Main" rather than "MainKt". This is to prevent
 * any issues/confusion if this file is ever replaced with a Java class.
 * See the [Package Level Functions](https://kotlinlang.org/docs/java-to-kotlin-interop.html#package-level-functions)
 * section on the *Calling Kotlin from Java* page of the Kotlin Docs.
 *
 * If you change your main frc.team2471.frc2026.Robot object (name), change the parameter of the
 * `RobotBase.startRobot` call below to the new name. (If you use the IDE's
 * Rename * Refactoring when renaming the object, it will get changed everywhere
 * including here.)
 */
fun main() = RobotBase.startRobot { Robot }
