package com.arcanjodev.cofrinho.application.usecase

import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementType
import com.arcanjodev.cofrinho.domain.repository.MovementRepository
import java.math.BigDecimal

class RegisterDepositUseCase(
    private val movementRepository: MovementRepository
) {
    fun execute(command: RegisterMovementCommand): Movement {
        val movement = Movement.create(
            description = command.description,
            amount = command.amount,
            type = MovementType.DEPOSIT
        )

        return movementRepository.save(movement)
    }
}

data class RegisterMovementCommand(
    val description: String,
    val amount: BigDecimal
)
