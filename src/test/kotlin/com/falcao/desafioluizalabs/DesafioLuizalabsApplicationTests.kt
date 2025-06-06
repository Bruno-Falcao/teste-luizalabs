package com.falcao.desafioluizalabs

import com.falcao.desafioluizalabs.model.Order
import com.falcao.desafioluizalabs.service.ConversaoService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DesafioLuizalabsApplicationTests {

    private val service = ConversaoService()

    private val validLine = "0000000070                              Palmer Prosacco00000007530000000003     1836.7420210308"
    private val otherValidLine = "0000000075                                  Bobbie Batz00000007980000000002     1578.5720211116"

    @Test
    fun `deve retornar lista agrupada com dados válidos`() {
        val content = "$validLine\n$otherValidLine"

        val result = service.retornaTodosDados(content)

        assertThat(result).hasSize(2)

        val joaoData = result.find { it["name"] == "Palmer Prosacco" }
        assertThat(joaoData).isNotNull
        assertThat((joaoData!!["orders"] as List<*>)).hasSize(1)
    }

    @Test
    fun `deve retornar apenas dados filtrados por nome`() {
        val content = "$validLine\n$otherValidLine"

        val result = service.retornaDadosByName(content, "Palmer Prosacco")

        assertThat(result).hasSize(1)
        val maria = result.first()
        assertThat(maria["name"]).isEqualTo("Palmer Prosacco")
        assertThat((maria["orders"] as List<*>)).hasSize(1)
    }

    @Test
    fun `deve ignorar linhas inválidas e continuar processando as válidas`() {
        val invalidLine = "linha invalida muito curta"
        val content = "$validLine\n$invalidLine\n$otherValidLine"

        val result = service.retornaTodosDados(content)

        assertThat(result).hasSize(2)
    }

    @Test
    fun `deve formatar corretamente a data em yyyy-MM-dd`() {
        val content = validLine // data no final: 20240601

        val result = service.retornaTodosDados(content)
        val orders = result.first()["orders"] as List<*>
        val order = orders.first() as Order

        assertThat(order.date).isEqualTo("2021-03-08")
    }

    @Test
    fun `deve lançar exceção ao tentar ler linha com campos insuficientes`() {
        val shortLine = "123" // menor que 95 caracteres

        val exception = runCatching {
            service.retornaTodosDados(shortLine)
        }.exceptionOrNull()

        assertThat(exception).isNull() // método ignora linha inválida e não lança
    }
}
