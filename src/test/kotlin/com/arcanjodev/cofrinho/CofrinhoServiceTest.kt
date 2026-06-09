package com.arcanjodev.cofrinho

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CofrinhoServiceTest {
    @Test
    fun `deve somar depositos e subtrair saques`() {
        val service = CofrinhoService()

        service.deposit(MoneyRequest(descricao = "Mesada", valor = 100.0))
        service.withdraw(MoneyRequest(descricao = "Livro", valor = 25.0))

        val summary = service.summary()

        assertEquals(75.0, summary.balance)
        assertEquals(100.0, summary.totalSaved)
        assertEquals(2, summary.movements.size)
    }

    @Test
    fun `nao deve sacar mais que o saldo disponivel`() {
        val service = CofrinhoService()

        assertFailsWith<IllegalArgumentException> {
            service.withdraw(MoneyRequest(descricao = "Compra", valor = 10.0))
        }
    }
}
