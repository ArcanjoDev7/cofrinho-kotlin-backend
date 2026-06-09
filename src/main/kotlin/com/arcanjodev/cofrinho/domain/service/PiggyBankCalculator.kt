package com.arcanjodev.cofrinho.domain.service

import com.arcanjodev.cofrinho.domain.model.Money
import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementType
import com.arcanjodev.cofrinho.domain.model.PiggyBankSummary

class PiggyBankCalculator {
    fun summarize(movements: List<Movement>): PiggyBankSummary {
        val orderedMovements = movements.sortedByDescending { it.createdAt }
        val balance = orderedMovements.fold(Money.ZERO) { total, movement ->
            when (movement.type) {
                MovementType.DEPOSIT -> Money.unsafe(total.value + movement.amount.value)
                MovementType.WITHDRAW -> Money.unsafe(total.value - movement.amount.value)
            }
        }
        val totalSaved = orderedMovements
            .filter { it.type == MovementType.DEPOSIT }
            .fold(Money.ZERO) { total, movement -> Money.unsafe(total.value + movement.amount.value) }

        return PiggyBankSummary(
            balance = balance,
            totalSaved = totalSaved,
            movements = orderedMovements
        )
    }
}
