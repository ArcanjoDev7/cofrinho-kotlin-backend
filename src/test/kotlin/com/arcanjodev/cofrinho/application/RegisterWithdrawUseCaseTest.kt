package com.arcanjodev.cofrinho.application

import com.arcanjodev.cofrinho.application.exception.InsufficientBalanceException
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import com.arcanjodev.cofrinho.adapter.out.persistence.InMemoryMovementRepository
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RegisterWithdrawUseCaseTest {
    @Test
    fun `should create withdraw movement when balance is sufficient`() {
        val repository = InMemoryMovementRepository()
        val deposit = RegisterDepositUseCase(repository)
        val withdraw = RegisterWithdrawUseCase(repository)

        val dep = deposit.execute(RegisterMovementCommand("Allowance", BigDecimal("50.00")))
        val w = withdraw.execute(RegisterMovementCommand(dep.id.value, BigDecimal("25.00")))

        // withdraw description should match referenced deposit description
        assertEquals("Allowance", w.description)
        assertEquals(25.0, w.amount.toDouble())
    }

    @Test
    fun `should throw when withdrawing more than balance`() {
        val repository = InMemoryMovementRepository()
        val withdraw = RegisterWithdrawUseCase(repository)

        assertFailsWith<InsufficientBalanceException> {
            withdraw.execute(RegisterMovementCommand("NoMoney", BigDecimal("10.00")))
        }
    }
}

