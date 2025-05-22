package net.eupixel.config

data object Config {
    val dockerHost: String = "npipe:////./pipe/docker_engine"
    val lobbyImage: String = "anton691/lovib:latest"
    val baseLobbies: Int = 3
}