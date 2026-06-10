package com.arcanjodev.cofrinho.infra

import com.arcanjodev.cofrinho.infra.repository.InMemoryMovementRepository
import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementType
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryMovementRepositoryTest {
    @Test
    fun `save and find by id`() {
        val repo = InMemoryMovementRepository()

        val movement = Movement.create("Test", BigDecimal("12.34"), MovementType.DEPOSIT)
        repo.save(movement)

        val found = repo.findById(movement.id)
        assertEquals(movement, found)
    }

    @Test
    fun `delete removes movement`() {
        val repo = InMemoryMovementRepository()
        val movement = Movement.create("T", BigDecimal("1.00"), MovementType.DEPOSIT)
        repo.save(movement)

        val deleted = repo.deleteById(movement.id)
        assertEquals(true, deleted)

        val found = repo.findById(movement.id)
        assertNull(found)
    }
}

