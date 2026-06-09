package com.arcanjodev.cofrinho.domain.repository

import com.arcanjodev.cofrinho.domain.model.Movement
import com.arcanjodev.cofrinho.domain.model.MovementId

interface MovementRepository {
    fun findAll(): List<Movement>
    fun save(movement: Movement): Movement
    fun deleteById(id: MovementId): Boolean
}
