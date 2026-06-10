package com.arcanjodev.cofrinho.adapter.inbound.web

import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import org.slf4j.LoggerFactory

class PiggyBankController(
    private val getSummary: GetPiggyBankSummaryUseCase,
    private val registerDeposit: RegisterDepositUseCase,
    private val registerWithdraw: RegisterWithdrawUseCase,
    private val deleteMovement: DeleteMovementUseCase
) {
    private val logger = LoggerFactory.getLogger(PiggyBankController::class.java)

    fun getSummary(): PiggyBankSummaryResponse {
        val summary = getSummary.execute()
        logger.info("GET /api/cofrinho - completed successfully, status={}", 200)
        return PiggyBankSummaryResponse.from(summary)
    }

    fun registerDeposit(request: MoneyRequest): MovementResponse {
        logger.debug("POST /api/depositos - payload: {}", request)
        val movement = registerDeposit.execute(request.toCommand())
        logger.info("POST /api/depositos - completed successfully, status={}, id={}", 201, movement.id.value)
        return MovementResponse.from(movement)
    }

    fun registerWithdraw(request: WithdrawRequest): MovementResponse {
        logger.debug("POST /api/saques - payload: {}", request)
        val movement = registerWithdraw.execute(request.toCommand())
        logger.info("POST /api/saques - completed successfully, status={}, id={}", 201, movement.id.value)
        return MovementResponse.from(movement)
    }

    fun deleteMovement(id: String) {
        deleteMovement.execute(id)
        logger.info("DELETE /api/movimentacoes/{} - completed successfully", id)
    }
}

