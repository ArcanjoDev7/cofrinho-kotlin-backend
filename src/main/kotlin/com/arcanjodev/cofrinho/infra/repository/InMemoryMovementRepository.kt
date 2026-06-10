package com.arcanjodev.cofrinho.infra.repository

import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementId
import com.arcanjodev.cofrinho.domain.repository.MovementRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryMovementRepository : MovementRepository {
    private val movements = ConcurrentHashMap<String, Movement>()

    override fun findAll(): List<Movement> = movements.values.toList()

    override fun findById(id: MovementId): Movement? = movements[id.value]

    override fun save(movement: Movement): Movement {
        movements[movement.id.value] = movement
        return movement
    }

    override fun deleteById(id: MovementId): Boolean = movements.remove(id.value) != null
}
