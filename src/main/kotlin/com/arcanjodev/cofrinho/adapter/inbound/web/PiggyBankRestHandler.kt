package com.arcanjodev.cofrinho.adapter.inbound.web

import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterMovementCommand
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class PiggyBankRestHandler(
    private val getSummary: GetPiggyBankSummaryUseCase,
    private val registerDeposit: RegisterDepositUseCase,
    private val registerWithdraw: RegisterWithdrawUseCase,
    private val deleteMovement: DeleteMovementUseCase
) {
    private val logger = LoggerFactory.getLogger(PiggyBankRestHandler::class.java)

    fun getSummary(): PiggyBankSummaryResponse {
        val summary = getSummary.execute()
        logger.info("GET /api/cofrinho - completed successfully, status={}", 200)
        return PiggyBankSummaryResponse.from(summary)
    }

    fun registerDeposit(request: DepositRequest): MovementResponse {
        logger.debug("POST /api/depositos - payload: {}", request)
        val command = RegisterMovementCommand(request.descricao, BigDecimal.valueOf(request.valor))
        val movement = registerDeposit.execute(command)
        logger.info("POST /api/depositos - completed successfully, status={}, id={}", 201, movement.id.value)
        return MovementResponse.from(movement)
    }

    fun registerWithdraw(request: WithdrawRequest): MovementResponse {
        logger.debug("POST /api/saques - payload: {}", request)
        val description = if (request.description.isBlank()) request.id else request.description
        val command = RegisterMovementCommand(description, BigDecimal.valueOf(request.valor))
        val movement = registerWithdraw.execute(command)
        logger.info("POST /api/saques - completed successfully, status={}, id={}", 201, movement.id.value)
        return MovementResponse.from(movement)
    }

    fun deleteMovement(id: String) {
        deleteMovement.execute(id)
        logger.info("DELETE /api/movimentacoes/{} - completed successfully", id)
    }
}

