package net.eupixel.util

object Config {
    var lobbyImage: String = "anton691/lobby:latest"
    val entryHost = System.getenv("ENTRY_HOST") ?: "localhost"
}