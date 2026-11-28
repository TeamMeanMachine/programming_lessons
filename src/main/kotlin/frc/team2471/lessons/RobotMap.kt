@file:Suppress("unused")

package frc.team2471.lessons

import com.ctre.phoenix6.CANBus
import org.wpilib.hardware.bus.CANPort

object Sparks { // SPARK REV motors
}

object Talons { // TalonFX CTRE motors
    const val FRONT_RIGHT_DRIVE = 23
    const val FRONT_RIGHT_STEER = 22

    const val FRONT_LEFT_DRIVE = 16
    const val FRONT_LEFT_STEER = 15

    const val BACK_RIGHT_DRIVE = 1
    const val BACK_RIGHT_STEER = 24

    const val BACK_LEFT_DRIVE = 10
    const val BACK_LEFT_STEER = 11

    const val INTAKE_PIVOT = 55
    const val ELEVATOR_0 = 60
    const val ELEVATOR_1 = 59
    const val EXTENSION = 58
}

object CANCoders {
    const val FRONT_RIGHT = 31
    const val FRONT_LEFT = 30
    const val BACK_RIGHT = 32
    const val BACK_LEFT = 33
}

object PWMPort {
}

object ServoPort {
}

object CANBusses {
    // CAN buses on Systemcore. For CTRE CAN devices
    val CAN_0 = CANBus(CANPort.CAN_S0)
    val CAN_1 = CANBus(CANPort.CAN_S1)
    val CAN_2 = CANBus(CANPort.CAN_S2)
    val CAN_3 = CANBus(CANPort.CAN_S3)
    val CAN_4 = CANBus(CANPort.CAN_S4)
}

object CANSensors {
    const val PIGEON = 34
}

object I2CPort {
}