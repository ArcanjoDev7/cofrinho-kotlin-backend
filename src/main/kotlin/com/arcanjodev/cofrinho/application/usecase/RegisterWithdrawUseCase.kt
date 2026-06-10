package com.arcanjodev.cofrinho.application.usecase

import com.arcanjodev.cofrinho.application.exception.InsufficientBalanceException
import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementId
import com.arcanjodev.cofrinho.domain.model.MovementType
import com.arcanjodev.cofrinho.domain.repository.MovementRepository
import com.arcanjodev.cofrinho.domain.service.PiggyBankCalculator

class RegisterWithdrawUseCase(
    private val movementRepository: MovementRepository,
    private val calculator: PiggyBankCalculator = PiggyBankCalculator()
) {
    fun execute(command: RegisterMovementCommand): Movement {
        // If the command.description refers to an existing movement id, use that movement's description
        val referenced = movementRepository.findById(MovementId(command.description))
        val descriptionToUse = referenced?.description ?: command.description

        val movement = Movement.create(
            description = descriptionToUse,
            amount = command.amount,
            type = MovementType.WITHDRAW
        )
        val currentBalance = calculator.summarize(movementRepository.findAll()).balance

        if (movement.amount > currentBalance) {
            throw InsufficientBalanceException("Saldo insuficiente para saque")
        }

        return movementRepository.save(movement)
    }
}
