package com.falcao.desafioluizalabs.service

import com.falcao.desafioluizalabs.model.Order
import com.falcao.desafioluizalabs.model.Product
import com.falcao.desafioluizalabs.model.User
import org.springframework.stereotype.Service

@Service
class ConversaoService {

    fun retornaTodosDados(content: String): List<Map<String, Any>> {
        val lines = content.lines().filter { it.isNotBlank() }
        val processedData = mutableListOf<User>()
        val errors = mutableListOf<String>()
        var processedLines = 0

        lines.forEachIndexed { index, line ->
            try {
                val pedido = leitorDeArquivo(line)
                processedData.add(pedido)
                processedLines++
            } catch (e: Exception) {
                errors.add("Linha ${index + 1}: ${e.message}")
            }
        }

        val result = groupByUser(processedData)
        return result
    }

    fun retornaDadosByName(content: String,name: String): List<Map<String, Any>> {
        val lines = content.lines().filter { it.isNotBlank() }
        val processedData = mutableListOf<User>()
        val errors = mutableListOf<String>()
        var processedLines = 0

        lines.forEachIndexed { index, line ->
            try {
                val pedido = leitorDeArquivo(line)
                processedData.add(pedido)
                processedLines++
            } catch (e: Exception) {
                errors.add("Linha ${index + 1}: ${e.message}")
            }
        }

        val result = groupByName(processedData,name)
        return result
    }

    private fun leitorDeArquivo(line: String): User {
        if (line.length < 95) { // Tamanho mínimo esperado
            throw IllegalArgumentException("Linha muito curta: ${line.length} caracteres")
        }

        try {
            val idUsuario = line.substring(0, 10).trim().toLong()

            val nome = line.substring(10, 55).trim()

            val idPedidoStr = line.substring(55, 65)
            val idPedido = idPedidoStr.toLong()

            val idProdutoStr = line.substring(65, 75)
            val idProduto = idProdutoStr.toLong()

            val valorProdutoStr = line.substring(75, 87).trim()
            val valorProduto = valorProdutoStr.toBigDecimal()

            val dataCompra = line.substring(87, 95).trim()
            val dataCompraFormatada = formatDateToDisplay(dataCompra)


            return User(
                userId = idUsuario,
                name = nome,
                orders = Order(orderId = idPedido, total = valorProduto, date = dataCompraFormatada,
                    products = Product(productId = idProduto, value = valorProduto)
                )
            )
        } catch (e: StringIndexOutOfBoundsException) {
            throw IllegalArgumentException("Erro ao extrair campos da linha")
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        if (dateStr.length != 8) return false

        return try {
            val year = dateStr.substring(0, 4).toInt()
            val month = dateStr.substring(4, 6).toInt()
            val day = dateStr.substring(6, 8).toInt()

            year in 1900..2100 && month in 1..12 && day in 1..31
        } catch (e: Exception) {
            false
        }
    }

    private fun groupByUser(data: List<User>): List<Map<String,Any>> {
        return data.groupBy { it.userId }
            .map { (userId, userPedidos) ->
                val userName = userPedidos.first().name
                val orders = userPedidos
                    .map { it.orders }

                mutableMapOf(
                    "userId" to userId,
                    "name" to userName,
                    "orders" to orders
                )
            }
    }

    private fun groupByName(data: List<User>,name: String): List<Map<String,Any>> {
        return data
            .filter { it.name == name }
            .groupBy { it.userId }
            .map { (userId, userPedidos) ->
                val userName = userPedidos.first().name
                val orders = userPedidos
                    .map { it.orders }

                mutableMapOf(
                    "userId" to userId,
                    "name" to userName,
                    "orders" to orders
                )
            }
    }

    private fun formatDateToDisplay(dateStr: String): String {
        // Converter yyyymmdd para yyyy-mm-dd
        return try {
            val year = dateStr.substring(0, 4)
            val month = dateStr.substring(4, 6)
            val day = dateStr.substring(6, 8)
            "$year-$month-$day"
        } catch (e: Exception) {
            dateStr // Retorna original se não conseguir converter
        }
    }

}