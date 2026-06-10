package com.arcanjodev.cofrinho.adapter.inbound.web

import com.arcanjodev.cofrinho.application.exception.InsufficientBalanceException
import com.arcanjodev.cofrinho.application.exception.MovementNotFoundException
import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import com.arcanjodev.cofrinho.adapter.inbound.web.PiggyBankRestHandler

private val logger = LoggerFactory.getLogger("com.arcanjodev.cofrinho.adapter.inbound.web.HttpRoutes")

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
            logger.warn("Bad request: {}", cause.message, cause)
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Requisicao invalida"))
        }
        exception<InsufficientBalanceException> { call, cause ->
            logger.warn("Saldo insuficiente: {}", cause.message, cause)
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Saldo insuficiente"))
        }
        exception<MovementNotFoundException> { call, cause ->
            logger.info("Movimentacao nao encontrada: {}", cause.message, cause)
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Nao encontrado"))
        }
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception while processing request {} {}", call.request.local.method, call.request.uri, cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro interno"))
        }
    }

    val handler = PiggyBankRestHandler(getSummary, registerDeposit, registerWithdraw, deleteMovement)

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/api") {
            get("/cofrinho") {
                val response = handler.getSummary()
                call.respond(response)
            }

            post("/depositos") {
                val request = call.receive<DepositRequest>()
                val response = handler.registerDeposit(request)
                call.respond(HttpStatusCode.Created, response)
            }

            post("/saques") {
                val request = call.receive<WithdrawRequest>()
                val response = handler.registerWithdraw(request)
                call.respond(HttpStatusCode.Created, response)
            }

            delete("/movimentacoes/{id}") {
                val id = call.parameters["id"].orEmpty()
                handler.deleteMovement(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

