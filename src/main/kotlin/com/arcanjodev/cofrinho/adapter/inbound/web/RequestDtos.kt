package com.arcanjodev.cofrinho.adapter.inbound.web

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DepositRequest(
    val descricao: String,
    val valor: Double
)

@Serializable
data class WithdrawRequest(
    val id: String,
    val description: String = "",
    val valor: Double
)

