package net.eupixel.core

import net.eupixel.su
import net.eupixel.config.Config
import net.eupixel.model.Server
import net.eupixel.sr

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