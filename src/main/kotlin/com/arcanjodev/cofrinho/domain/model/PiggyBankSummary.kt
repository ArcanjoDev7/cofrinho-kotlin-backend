package com.arcanjodev.cofrinho.domain.model

data class PiggyBankSummary(
    val balance: Money,
    val totalSaved: Money,
    val movements: List<Movement>
)
