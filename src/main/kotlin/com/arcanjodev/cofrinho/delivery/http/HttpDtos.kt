package com.arcanjodev.cofrinho.delivery.http

import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementType
import com.arcanjodev.cofrinho.domain.model.PiggyBankSummary
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class MoneyRequest(
    val descricao: String,
    val valor: Double
) {
    fun toCommand(): RegisterMovementCommand = RegisterMovementCommand(
        description = descricao,
        amount = BigDecimal.valueOf(valor)
    )
}

@Serializable
data class PiggyBankSummaryResponse(
    val balance: Double,
    val totalSaved: Double,
    val movements: List<MovementResponse>
) {
    companion object {
        fun from(summary: PiggyBankSummary): PiggyBankSummaryResponse = PiggyBankSummaryResponse(
            balance = summary.balance.toDouble(),
            totalSaved = summary.totalSaved.toDouble(),
            movements = summary.movements.map(MovementResponse::from)
        )
    }
}

@Serializable
data class MovementResponse(
    val id: String,
    val description: String,
    val amount: Double,
    val type: MovementTypeResponse,
    val createdAt: String
) {
    companion object {
        fun from(movement: Movement): MovementResponse = MovementResponse(
            id = movement.id.value,
            description = movement.description,
            amount = movement.amount.toDouble(),
            type = MovementTypeResponse.from(movement.type),
            createdAt = movement.createdAt.toString()
        )
    }
}

@Serializable
enum class MovementTypeResponse {
    DEPOSIT,
    WITHDRAW;

    companion object {
        fun from(type: MovementType): MovementTypeResponse = when (type) {
            MovementType.DEPOSIT -> DEPOSIT
            MovementType.WITHDRAW -> WITHDRAW
        }
    }
}

@Serializable
data class ErrorResponse(
    val message: String
)
