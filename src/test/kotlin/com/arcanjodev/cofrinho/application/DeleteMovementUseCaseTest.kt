package com.arcanjodev.cofrinho.application

import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import com.arcanjodev.cofrinho.infra.repository.InMemoryMovementRepository
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DeleteMovementUseCaseTest {
    @Test
    fun `should delete existing movement`() {
        val repository = InMemoryMovementRepository()
        val deposit = RegisterDepositUseCase(repository)
        val delete = DeleteMovementUseCase(repository)

        val dep = deposit.execute(RegisterMovementCommand("Gift", BigDecimal("30.00")))
        delete.execute(dep.id.value)

        val found = repository.findById(dep.id)
        assertEquals(null, found)
    }

    @Test
    fun `deleting non existing movement should throw`() {
        val repository = InMemoryMovementRepository()
        val delete = DeleteMovementUseCase(repository)

        assertFails { delete.execute("non-existent-id") }
    }
}

