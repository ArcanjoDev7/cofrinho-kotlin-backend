package com.arcanjodev.cofrinho.application.usecase

import com.arcanjodev.cofrinho.domain.model.PiggyBankSummary
import com.arcanjodev.cofrinho.domain.repository.MovementRepository
import com.arcanjodev.cofrinho.domain.service.PiggyBankCalculator

class GetPiggyBankSummaryUseCase(
    private val movementRepository: MovementRepository,
    private val calculator: PiggyBankCalculator = PiggyBankCalculator()
) {
    fun execute(): PiggyBankSummary = calculator.summarize(movementRepository.findAll())
}
