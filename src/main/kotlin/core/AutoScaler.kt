package dev.aquestry.core

import dev.aquestry.su
import dev.aquestry.config.Config
import dev.aquestry.model.Server

class AutoScaler {
    suspend fun start() {
        repeat(Config.baseLobbies) {
            su.createServer(Config.lobbyImage, "lobby")
        }
    }

    fun getLobby(): Server {
        return su.getType("lobby")
    }
}