package com.arcanjodev.cofrinho.application

import com.arcanjodev.cofrinho.application.exception.InsufficientBalanceException
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import com.arcanjodev.cofrinho.infra.repository.InMemoryMovementRepository
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PiggyBankUseCaseTest {
    @Test
    fun `deve somar depositos e subtrair saques`() {
        val repository = InMemoryMovementRepository()
        val registerDeposit = RegisterDepositUseCase(repository)
        val registerWithdraw = RegisterWithdrawUseCase(repository)
        val getSummary = GetPiggyBankSummaryUseCase(repository)

        registerDeposit.execute(RegisterMovementCommand("Mesada", BigDecimal("100.00")))
        registerWithdraw.execute(RegisterMovementCommand("Livro", BigDecimal("25.00")))

        val summary = getSummary.execute()

        assertEquals(75.0, summary.balance.toDouble())
        assertEquals(100.0, summary.totalSaved.toDouble())
        assertEquals(2, summary.movements.size)
    }

    @Test
    fun `nao deve sacar mais que o saldo disponivel`() {
        val repository = InMemoryMovementRepository()
        val registerWithdraw = RegisterWithdrawUseCase(repository)

        assertFailsWith<InsufficientBalanceException> {
            registerWithdraw.execute(RegisterMovementCommand("Compra", BigDecimal("10.00")))
        }
    }
}
