package frc.team2471.lessons

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team2471.frc.lib.hardware.ctre.applyConfiguration
import org.team2471.frc.lib.hardware.ctre.brakeMode
import org.team2471.frc.lib.hardware.ctre.currentLimits

@Suppress("Unused", "Redundant")


//Creates a subsystem object
object IntakeSubsystem : SubsystemBase() {
    /** Make an intake motor controller variable and define it's ID */
    val intakeMotor = TalonFX(Talons.TEST_MOTOR)

    /** Configure the motor with current limits for safety. (inside init) */
    init {
        //apply a motor configuration
        intakeMotor.applyConfiguration {
            currentLimits(5.0, 10.0, 1.0)
            brakeMode()
        }

        /** Alternate ways to configure the motor. These do the same thing as above.
         * (Useful when trying to configure new or obscure configs that don't have 'shortcuts' added inside Meanlib) */
        /*
        intakeMotor.applyConfiguration {
            CurrentLimits.apply {
                SupplyCurrentLimitEnable = true
                SupplyCurrentLimit = 10.0
                SupplyCurrentLimit = 5.0
                SupplyCurrentLowerTime = 1.0
            }
            MotorOutput.apply {
                NeutralMode = NeutralModeValue.Brake
            }
        }

        intakeMotor.configurator.apply(TalonFXConfiguration().apply {
            CurrentLimits.apply {
                SupplyCurrentLimitEnable = true
                SupplyCurrentLimit = 10.0
                SupplyCurrentLimit = 5.0
                SupplyCurrentLowerTime = 1.0
            }
            MotorOutput.apply {
                NeutralMode = NeutralModeValue.Brake
            }
        })
        */

    }

    /** Tell motor to run at [percentage] power */
    fun setPower(percentage: Double) {
        intakeMotor.setControl(DutyCycleOut(percentage))
    }

    /** Tell motor to run at [voltage] volts */
    fun setVoltage(voltage: Double) {
        intakeMotor.setControl(VoltageOut(voltage))
    }
}