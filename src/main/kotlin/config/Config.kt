package dev.aquestry.config

data object Config {
    val dockerUri: String = "npipe:////./pipe/docker_engine"
    val lobbyImage: String = "itzg/minecraft-server:latest"
    val baseLobbies: Int = 3
    val maxLobbies: Int = 10
    val maxLobbyPlayers: Int = 10
    val scaleUpThreshold: Int = 3
    val minPort: Int = 25566
    val maxPort: Int = 25570
}