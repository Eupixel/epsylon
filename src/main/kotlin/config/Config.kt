package dev.aquestry.config

data object Config {
    val dockerHost: String = "npipe:////./pipe/docker_engine"
    val lobbyImage: String = "anton691/lovib:latest"
    val baseLobbies: Int = 3
    val maxLobbies: Int = 10
    val maxLobbyPlayers: Int = 10
    val scaleUpThreshold: Int = 3
}