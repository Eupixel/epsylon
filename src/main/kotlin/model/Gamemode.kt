package net.eupixel.model

data class Gamemode(
    val name: String,
    val friendlyName: String,
    val image: String,
    val playerCounts: Array<String>
)