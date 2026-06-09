package com.arcanjodev.cofrinho

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

fun Application.module() {
    val service = CofrinhoService()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Requisicao invalida"))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Nao encontrado"))
        }
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/api") {
            get("/cofrinho") {
                call.respond(service.summary())
            }

            post("/depositos") {
                val request = call.receive<MoneyRequest>()
                call.respond(HttpStatusCode.Created, service.deposit(request))
            }

            post("/saques") {
                val request = call.receive<MoneyRequest>()
                call.respond(HttpStatusCode.Created, service.withdraw(request))
            }

            delete("/movimentacoes/{id}") {
                val id = call.parameters["id"].orEmpty()
                service.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

class CofrinhoService {
    private val movements = ConcurrentHashMap<String, Movement>()

    fun summary(): CofrinhoSummary {
        val orderedMovements = movements.values.sortedByDescending { it.createdAt }
        val balance = orderedMovements.fold(BigDecimal.ZERO) { total, movement ->
            when (movement.type) {
                MovementType.DEPOSIT -> total + movement.amount.toBigDecimal()
                MovementType.WITHDRAW -> total - movement.amount.toBigDecimal()
            }
        }

        val saved = orderedMovements
            .filter { it.type == MovementType.DEPOSIT }
            .fold(BigDecimal.ZERO) { total, movement -> total + movement.amount.toBigDecimal() }

        return CofrinhoSummary(
            balance = balance.toDouble(),
            totalSaved = saved.toDouble(),
            movements = orderedMovements
        )
    }

    fun deposit(request: MoneyRequest): Movement {
        val movement = request.toMovement(MovementType.DEPOSIT)
        movements[movement.id] = movement
        return movement
    }

    fun withdraw(request: MoneyRequest): Movement {
        val movement = request.toMovement(MovementType.WITHDRAW)
        val currentBalance = summary().balance.toBigDecimal()

        if (movement.amount.toBigDecimal() > currentBalance) {
            throw IllegalArgumentException("Saldo insuficiente para saque")
        }

        movements[movement.id] = movement
        return movement
    }

    fun delete(id: String) {
        if (movements.remove(id) == null) {
            throw NotFoundException("Movimentacao nao encontrada")
        }
    }

    private fun MoneyRequest.toMovement(type: MovementType): Movement {
        val cleanDescription = descricao.trim()
        val cleanAmount = valor.toBigDecimal()

        require(cleanDescription.isNotBlank()) { "Descricao e obrigatoria" }
        require(cleanAmount > BigDecimal.ZERO) { "Valor deve ser maior que zero" }

        return Movement(
            id = UUID.randomUUID().toString(),
            description = cleanDescription,
            amount = cleanAmount.toDouble(),
            type = type,
            createdAt = Instant.now().toString()
        )
    }
}

@Serializable
data class MoneyRequest(
    val descricao: String,
    val valor: Double
)

@Serializable
data class CofrinhoSummary(
    val balance: Double,
    val totalSaved: Double,
    val movements: List<Movement>
)

@Serializable
data class Movement(
    val id: String,
    val description: String,
    val amount: Double,
    val type: MovementType,
    val createdAt: String
)

@Serializable
enum class MovementType {
    DEPOSIT,
    WITHDRAW
}

@Serializable
data class ErrorResponse(
    val message: String
)

class NotFoundException(message: String) : RuntimeException(message)
