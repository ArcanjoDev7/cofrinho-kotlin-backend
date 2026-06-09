package com.arcanjodev.cofrinho.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Movement(
    val id: MovementId,
    val description: String,
    val amount: Money,
    val type: MovementType,
    val createdAt: Instant
) {
    companion object {
        fun create(description: String, amount: BigDecimal, type: MovementType): Movement {
            val cleanDescription = description.trim()

            require(cleanDescription.isNotBlank()) { "Descricao e obrigatoria" }

            return Movement(
                id = MovementId(UUID.randomUUID().toString()),
                description = cleanDescription,
                amount = Money.of(amount),
                type = type,
                createdAt = Instant.now()
            )
        }
    }
}

@JvmInline
value class MovementId(val value: String)

enum class MovementType {
    DEPOSIT,
    WITHDRAW
}
