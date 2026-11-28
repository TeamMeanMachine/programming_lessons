package frc.team2471.lessons

import choreo.util.ChoreoAllianceFlipUtil
import org.team2471.frc.lib.autonomous.Autonomi
import org.team2471.frc.lib.autonomous.auto.AutoRoutine
import org.team2471.frc.lib.autonomous.test.TestRoutine
import org.team2471.frc.lib.commands.command
import org.team2471.frc.lib.commands.periodicTimeout
import org.team2471.frc.lib.commands.priority
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.swerve.mirrorX
import org.team2471.frc.lib.swerve.mirrorY
import org.team2471.frc.lib.swerve.rotateAround
import org.wpilib.command3.Command
import org.wpilib.command3.Scheduler
import org.wpilib.hardware.hal.RobotMode
import org.wpilib.math.geometry.Pose2d


object Autonomous: Autonomi() {

//    val paths: MutableMap<String, Trajectory<SwerveSample>> = findChoreoPaths()  <-- already inside AutoMaker

    /** All auto routines for Autonomous mode. Gets registered as an OpMode. [org.team2471.frc.lib.autonomous.auto.AutoOpMode] */
    override val autos: List<AutoRoutine> = listOf(
        // AutoRoutine( Name, Command, { RobotStartingPose }, { DisabledPeriodicLoop } )
        // AutoRoutine("My Cool Auto", myCoolAutoCommand(), { paths["coolPath"]!!.getInitialPose(Drive.flipChoreoPaths) }, { Drive.driveVelocity(paths["coolPath"]!!.getInitialSample(Drive.flipChoreoPaths).chassisSpeeds) }
        AutoRoutine("Eight Foot Straight", eightFootStraight()),
        AutoRoutine("Square Path Test", squarePathTest()),
        AutoRoutine("Print for 20 seconds", printFor20Seconds()),
        AutoRoutine("Print Hello", printHelloCommand()),
    )

    /** All test routines for Utility mode. Gets registered as an OpMode. */
    override val tests: List<TestRoutine> = listOf(
        // TestRoutine( Name, Command, { initFunction } )
        TestRoutine("Drive Set Angle Offsets", Drive.setAngleOffsets(), { Robot.disableAllDefaultCommands() }),
    )

    /** Supplier that sets the robot's pose. */
    override val drivePoseSetter: (Pose2d) -> Unit = { Drive.pose = it }

    /** Warmup function for speeding up auto loop times. Runs when selected auto changes. */
    override val warmupFunction: () -> Unit = {
        println("scheduling auto warmup")
        Scheduler.getDefault().schedule(warmupDriveAlongPath())
        println("finished scheduling auto warmup")
    }


    init {
        println("Autonomous init. Path count: ${paths.size} Auto count: ${autos.size} Test count: ${tests.size}")

        // Override choreo red/blue alliance flipper type, this is used a lot in SwerveDrive. (Usually FRC_CURRENT)
        // Used if the field is mirrored/rotated differently than what choreo thinks.
        // In cases where the in-season field red/blue reflection is different from offseason comps w/ different fields like BunnyBots
        ChoreoAllianceFlipUtil.setFlipper(ChoreoAllianceFlipUtil.Flipper.FRC_CURRENT)

        // Convert AutoRoutines to AutoOpModeSuppliers
        autos.toAutoOpModeSuppliers().forEach {
            println("Auto: ${it.name}")
            Robot.addOpMode(RobotMode.AUTONOMOUS, it.name, it.opModeSupplier) // Add autos to driver station
        }

        tests.toTestOpModeSuppliers().forEach { // Add Tests/Utility OpModes.
            println("Test: ${it.name}")
            Robot.addOpMode(RobotMode.UTILITY, it.name, it.opModeSupplier)
        }

        Robot.publishOpModes() // Publish cached OpModes to Driver Station. (This is most important)
    }

    /** Autonomous commands */

    fun printHelloCommand() = command("printHello") {
        println("hello")
    }


    private fun printFor20Seconds() = command("printFor20Seconds", Drive) {
        println("starting printFor20Seconds")
        periodicTimeout(20.0) {
            println("dt time: ${it.round(2)}")
        }
        println("finished")
    }

    private fun eightFootStraight() = command("eightFootStraight", Drive) {
        await(Drive.driveAlongChoreoPathCmd(paths["eightFoot"]!!, resetOdometry = true))
    }

    private fun squarePathTest() = command("squarePathTest", Drive) {
        await(Drive.driveAlongChoreoPathCmd(paths["square"]!!, resetOdometry = true))
    }


    fun warmupDriveAlongPath() = command("Warmup Drive Along Path", Drive) {
        val warmupPath = paths["eightFoot"]!!.mirrorY(true).mirrorX(true).rotateAround(true).flipped()
        await(Drive.driveAlongChoreoPathCmd(warmupPath.getSplit(0).get(), exitSupplier = { percent, error -> percent >= 1.0 || Robot.isEnabled}))
        println("Warmup DAL")
    }.priority(Command.LOWEST_PRIORITY + 1)
}