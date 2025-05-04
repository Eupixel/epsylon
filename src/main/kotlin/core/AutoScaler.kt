package dev.aquestry.core

import dev.aquestry.su
import dev.aquestry.config.Config
import dev.aquestry.model.Server
import dev.aquestry.sr

class AutoScaler {
    suspend fun start() {
        repeat(Config.baseLobbies) {
            su.createServer(Config.lobbyImage, "lobby")
        }
    }

    fun getLobby(): Server {
        var lobby = sr.getServers()[0]
        sr.getServers().forEach {
            if(it.players < lobby.players) {
                lobby = it
            }
        }
        return lobby
    }
}