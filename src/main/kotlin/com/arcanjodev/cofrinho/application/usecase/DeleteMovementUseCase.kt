package com.arcanjodev.cofrinho.application.usecase

import com.arcanjodev.cofrinho.application.exception.MovementNotFoundException
import com.arcanjodev.cofrinho.domain.model.MovementId
import com.arcanjodev.cofrinho.application.port.out.MovementRepository

class DeleteMovementUseCase(
    private val movementRepository: MovementRepository
) {
    fun execute(id: String) {
        val deleted = movementRepository.deleteById(MovementId(id))

        if (!deleted) {
            throw MovementNotFoundException("Movimentacao nao encontrada")
        }
    }
}
