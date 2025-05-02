package dev.aquestry.model

data class Server(
    val id: String,
    val type: String,
    val image: String,
    val host: String,
    val port: Int,
    var players: Int,
    var state: Boolean,
)
