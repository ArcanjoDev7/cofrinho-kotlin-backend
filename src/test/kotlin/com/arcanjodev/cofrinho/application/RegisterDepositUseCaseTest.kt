package com.arcanjodev.cofrinho.application

import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import com.arcanjodev.cofrinho.infra.repository.InMemoryMovementRepository
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterDepositUseCaseTest {
    @Test
    fun `should create deposit movement and persist it`() {
        val repository = InMemoryMovementRepository()
        val usecase = RegisterDepositUseCase(repository)

        val movement = usecase.execute(RegisterMovementCommand("Salary", BigDecimal("100.00")))

        assertEquals("Salary", movement.description)
        assertEquals(100.0, movement.amount.toDouble())
        // repository should contain the movement
        val found = repository.findById(movement.id)
        assertEquals(movement, found)
    }
}

