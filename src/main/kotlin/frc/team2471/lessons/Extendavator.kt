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
import org.wpilib.units.measure.Angle
import org.wpilib.units.measure.Distance

object Extendavator: MechanismBase("Extendavator") {

    // TODO: Create 3 LoggedTalonFX motors. elevatorMotor, extensionMotor, pivotMotor



    val MIN_EXTENSION_DIST = 22.0.inches
    val MAX_EXTENSION_DIST = 3.5.feet

    val MIN_ELEVATOR_HEIGHT = 0.0.inches
    val MAX_ELEVATOR_HEIGHT = 59.25.inches

    val MIN_PIVOT_ANGLE = -90.0.degrees
    val MAX_PIVOT_ANGLE = 90.0.degrees

    // TODO: Create elevatorSetpoint as a var and a setter to set the elevatorMotor to go to a position (PositionVoltage)




    // TODO: Create extensionSetpoint and pivotSetpoint. (remember that extension is in inches and pivot is in rotations)




    // TODO: Create elevatorHeight, extensionDistance, and pivotAngle getters. (Make sure they are in appropriate units, Distance and Angle)




    // TODO: Create heightError, extensionError, and pivotError getters. (Make sure they are in appropriate units, Distance and Angle)




    // TODO: Create totalErrorPose getter of type Pose and an atSetpoint Boolean getter.




    init {
        println("Extendavator init")


        // TODO: Configure elevator motor. Set current limits, a sensor-to-mechanism ratio of 0.187, and p value of 0.2



        // TODO: Add ELEVATOR_1 ID as a follower to elevatorMotor
        // TODO: Configure the elevatorMotor sim. With 2 KrackenX60s and a moi of 0.0001





        // TODO: Configure extension and pivot motors. Set current limits, sensor-to-mechanism ratios, p values, and simulation


        // TODO: extensionMotor has a sensor-to-mechanism ratio of 1.0/1.05, a p value of 0.5, 1 KrackenX44, and moi of 0.0001




        // TODO: pivotMotor has a sensor-to-mechanism ratio of 33.75, a p value of 17.0, 1 KrackenX44, and moi of 0.0001




    }


    // TODO: Write goToPoseCmd() command which calls the elevator, extension, and pivot setters.











    // TODO: Uncomment the lines below! After elevatorHeight, extensionDistance, and pivotAngle vals get created
//    @get:AutoLogOutput(key = "Components/measuredComponents")
//    val measuredComponents: Array<Pose3d> // Simulated motor components of extendavator
//        get() {
//            return createComponentArray(
//                0.0.degrees,
//                Extendavator.elevatorHeight,
//                Extendavator.extensionDistance - Extendavator.MIN_EXTENSION_DIST,
//                Extendavator.pivotAngle
//            )
//        }
//
//    @get:AutoLogOutput(key = "Components/setpointComponents")
//    val setpointComponents: Array<Pose3d> // Setpoint components of extendavator
//        get() {
//            return createComponentArray(
//                0.0.degrees,
//                Extendavator.elevatorSetpoint,
//                Extendavator.extensionSetpoint - Extendavator.MIN_EXTENSION_DIST,
//                Extendavator.pivotSetpoint
//            )
//        }

    /** Creates an array of Pose3d representing the components of the extendavator. For AdvantageScope visualization. */
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