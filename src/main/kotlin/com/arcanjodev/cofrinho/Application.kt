package com.arcanjodev.cofrinho

import com.arcanjodev.cofrinho.application.usecase.DeleteMovementUseCase
import com.arcanjodev.cofrinho.application.usecase.GetPiggyBankSummaryUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterDepositUseCase
import com.arcanjodev.cofrinho.application.usecase.RegisterWithdrawUseCase
import com.arcanjodev.cofrinho.adapter.inbound.web.configureHttp
import com.arcanjodev.cofrinho.adapter.out.persistence.InMemoryMovementRepository
import io.ktor.server.application.Application

fun Application.module() {
    val movementRepository = InMemoryMovementRepository()

    configureHttp(
        getSummary = GetPiggyBankSummaryUseCase(movementRepository),
        registerDeposit = RegisterDepositUseCase(movementRepository),
        registerWithdraw = RegisterWithdrawUseCase(movementRepository),
        deleteMovement = DeleteMovementUseCase(movementRepository)
    )
}
