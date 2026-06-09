package com.arcanjodev.cofrinho.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

class Money private constructor(val value: BigDecimal) {
    operator fun plus(other: Money): Money = unsafe(value + other.value)

    operator fun minus(other: Money): Money = unsafe(value - other.value)

    operator fun compareTo(other: Money): Int = value.compareTo(other.value)

    fun toDouble(): Double = value.toDouble()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Money) return false

        return value.compareTo(other.value) == 0
    }

    override fun hashCode(): Int = value.stripTrailingZeros().hashCode()

    override fun toString(): String = value.toPlainString()

    companion object {
        val ZERO: Money = Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))

        fun of(value: BigDecimal): Money {
            require(value > BigDecimal.ZERO) { "Valor deve ser maior que zero" }
            return Money(value.setScale(2, RoundingMode.HALF_UP))
        }

        fun unsafe(value: BigDecimal): Money = Money(value.setScale(2, RoundingMode.HALF_UP))
    }
}
