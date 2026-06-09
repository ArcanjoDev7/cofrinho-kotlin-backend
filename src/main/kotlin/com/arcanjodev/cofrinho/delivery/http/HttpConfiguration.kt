package com.arcanjodev.cofrinho.delivery.http

import com.arcanjodev.cofrinho.application.exception.InsufficientBalanceException
import com.arcanjodev.cofrinho.application.exception.MovementNotFoundException
import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
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
import kotlinx.serialization.json.Json

fun Application.configureHttp(
    getSummary: GetPiggyBankSummaryUseCase,
    registerDeposit: RegisterDepositUseCase,
    registerWithdraw: RegisterWithdrawUseCase,
    deleteMovement: DeleteMovementUseCase
) {
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
        exception<InsufficientBalanceException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Saldo insuficiente"))
        }
        exception<MovementNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Nao encontrado"))
        }
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/api") {
            get("/cofrinho") {
                call.respond(PiggyBankSummaryResponse.from(getSummary.execute()))
            }

            post("/depositos") {
                val request = call.receive<MoneyRequest>()
                val movement = registerDeposit.execute(request.toCommand())
                call.respond(HttpStatusCode.Created, MovementResponse.from(movement))
            }

            post("/saques") {
                val request = call.receive<MoneyRequest>()
                val movement = registerWithdraw.execute(request.toCommand())
                call.respond(HttpStatusCode.Created, MovementResponse.from(movement))
            }

            delete("/movimentacoes/{id}") {
                val id = call.parameters["id"].orEmpty()
                deleteMovement.execute(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
