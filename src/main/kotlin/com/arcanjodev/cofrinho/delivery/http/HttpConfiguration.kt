package com.arcanjodev.cofrinho.delivery.http

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

private val logger = LoggerFactory.getLogger("com.arcanjodev.cofrinho.delivery.http.HttpConfiguration")
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
        // Log unexpected errors so we can inspect stacktraces in the logs
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception while processing request {} {}", call.request.local.method, call.request.uri, cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro interno"))
        }
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/api") {
            get("/cofrinho") {
                val method = call.request.local.method
                val uri = call.request.uri
                val summary = getSummary.execute()
                call.respond(PiggyBankSummaryResponse.from(summary))
                logger.info("{} {} - completed successfully, status={}", method, uri, HttpStatusCode.OK.value)
            }

            post("/depositos") {
                val method = call.request.local.method
                val uri = call.request.uri
                val request = call.receive<MoneyRequest>()
                logger.debug("{} {} - payload: {}", method, uri, request)
                val movement = registerDeposit.execute(request.toCommand())
                call.respond(HttpStatusCode.Created, MovementResponse.from(movement))
                // MovementId is a value class; log the raw UUID string (movement.id.value)
                logger.info("{} {} - completed successfully, status={}, id={}", method, uri, HttpStatusCode.Created.value, movement.id.value)
            }

            post("/saques") {
                val method = call.request.local.method
                val uri = call.request.uri
                val request = call.receive<WithdrawRequest>()
                logger.debug("{} {} - payload: {}", method, uri, request)
                val movement = registerWithdraw.execute(request.toCommand())
                call.respond(HttpStatusCode.Created, MovementResponse.from(movement))
                // Log only the UUID string (not the data class description)
                logger.info("{} {} - completed successfully, status={}, id={}", method, uri, HttpStatusCode.Created.value, movement.id.value)
            }

            delete("/movimentacoes/{id}") {
                val id = call.parameters["id"].orEmpty()
                deleteMovement.execute(id)
                call.respond(HttpStatusCode.NoContent)
                logger.info("DELETE /api/movimentacoes/{} - completed successfully", id)
            }
        }
    }
}
