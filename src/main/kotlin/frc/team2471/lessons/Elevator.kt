package frc.team2471.lessons

import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.controls.MotionMagicDutyCycle
import com.ctre.phoenix6.controls.PositionDutyCycle
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.MotorAlignmentValue
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.hardware.ctre.addFollower
import org.team2471.frc.lib.hardware.ctre.applyConfiguration
import org.team2471.frc.lib.hardware.ctre.brakeMode
import org.team2471.frc.lib.hardware.ctre.currentLimits
import org.team2471.frc.lib.hardware.ctre.inverted
import org.team2471.frc.lib.hardware.ctre.motionMagic
import org.team2471.frc.lib.hardware.ctre.p
import org.team2471.frc.lib.units.asRotations
import org.team2471.frc.lib.units.degrees

@Suppress("unused")

object Elevator: SubsystemBase() {
    // Construct Motors
    val motor0 = TalonFX(10)
    val motor1 = TalonFX(11)
    val motor2 = TalonFX(12)
    val motor3 = TalonFX(13)


    // Store the motor setpoint for logging
    var motorSetpoint: Angle = 0.0.degrees

    // Store the motor angle
    val motorAngle: Angle
        get() = motor0.position.value


    init {
        println("Elevator init")

        motor0.applyConfiguration {
            currentLimits(20.0) // Amp limit of 20
            inverted(InvertedValue.Clockwise_Positive)
            brakeMode()
            // PID
            p(0.4)
            motionMagic(0.25, 1.0) // 0.25 Rot/s max velocity and 1.0 Rot/s^2 max acceleration
        }

        // Tell followers to follow leader (multiple ways, can use TalonFX object or device ID)
        motor0.addFollower(motor1, MotorAlignmentValue.Opposed)
        motor0.addFollower(motor2.deviceID, MotorAlignmentValue.Aligned)
        motor0.addFollower(13, MotorAlignmentValue.Opposed) // motor3
    }


    fun setPercentOut(percent: Double) {
        motor0.setControl(DutyCycleOut(percent))
        println("percentage: $percent")
    }

    fun setPosition(angle: Angle) {
        motor0.setControl(PositionDutyCycle(angle.asRotations))
        motorSetpoint = angle
    }

    fun setMotionMagic(angle: Angle) {
        motor0.setControl(MotionMagicDutyCycle(angle.asRotations))
        motorSetpoint = angle
    }


}