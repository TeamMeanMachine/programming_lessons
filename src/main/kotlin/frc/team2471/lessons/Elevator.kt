package frc.team2471.lessons

import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.controls.MotionMagicDutyCycle
import com.ctre.phoenix6.controls.PositionDutyCycle
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.MotorAlignmentValue
import edu.wpi.first.math.MathUtil
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.hardware.ctre.addFollower
import org.team2471.frc.lib.hardware.ctre.applyConfiguration
import org.team2471.frc.lib.hardware.ctre.brakeMode
import org.team2471.frc.lib.hardware.ctre.currentLimits
import org.team2471.frc.lib.hardware.ctre.inverted
import org.team2471.frc.lib.hardware.ctre.motionMagic
import org.team2471.frc.lib.hardware.ctre.p
import org.team2471.frc.lib.hardware.ctre.s
import org.team2471.frc.lib.units.asRotations
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.rotations

@Suppress("unused")

object Elevator: SubsystemBase() {

    //TODO: Create multiple motors (TalonFX)

    val motor0 = TalonFX(10)



    // TODO: Create motor setpoint and motor angle getter




    init {
        println("Elevator init")

        // TODO: Configure leader motor

        motor0.applyConfiguration {
            currentLimits(20.0)
            inverted(InvertedValue.Clockwise_Positive)
            brakeMode()
            // PID
            p(0.4)
            motionMagic(0.25, 1.0)
        }

        // TODO: Add followers to the leader motor

    }


    //TODO: Set percent (DutyCycleOut) output of motors




    // TODO: Set angle position of motors




    //TODO: Make a set motionMagic function that sets position using motionMagic




}