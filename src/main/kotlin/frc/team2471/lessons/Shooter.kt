package frc.team2471.lessons

import com.ctre.phoenix6.controls.VelocityVoltage
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.hardware.TalonFX
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.hardware.ctre.applyConfiguration
import org.team2471.frc.lib.hardware.ctre.currentLimits
import org.team2471.frc.lib.hardware.ctre.d
import org.team2471.frc.lib.hardware.ctre.p
import org.team2471.frc.lib.hardware.ctre.v

object Shooter : SubsystemBase() {

    // TODO: Create a network table with the name of the shooter subsystem

    // TODO: Create 3 entries.  One for shooter velocity, one for the velocity setpoint, and a third for the voltage applied




    // TODO: declare a TalonFX motor controller for the top shooter motor


    // TODO: declare a const val for MAX_VELOCITY and set it to 4000


    // TODO: create a var for the shooterVelocitySetpoint,
    //  include a set() function which sets the motors velocity by using VelocityVoltage mode






    // TODO: create a val for retrieving the velocity of the shooter motor,
    //  include a get() function to evaluate the velocity live continuously
    // TODO: be sure to convert CTRE's native units of Rotations per Second to ours Rotations per Minute




    init {
        // TODO: configure the top motor with current limits of 30 amps,
        //  include kP, kD, and kV values for our PID.  They can start at 0.






    }

    // TODO: make a setVoltage function for controlling the motor open loop, like Lesson 1





    // TODO: make a stop function which uses your setVoltage function to stop the motor





    override fun periodic() {
        // TODO: set the 3 network table entry values here periodically, so they are all up to date



    }
}