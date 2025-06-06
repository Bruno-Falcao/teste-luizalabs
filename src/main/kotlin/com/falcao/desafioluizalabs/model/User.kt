package com.falcao.desafioluizalabs.model

import java.math.BigDecimal

data class User(
    val userId: Long,
    val name: String,
    val orders:Order,
    )

data class Order(
    val orderId: Long,
    val total: BigDecimal,
    val date: String,
    val products:Product
)

data class Product(
    val productId: Long,
    val value: BigDecimal,
)