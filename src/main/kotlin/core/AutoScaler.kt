package net.eupixel.core

import net.eupixel.core.DirectusClient.getData
import net.eupixel.su
import net.eupixel.model.Server
import net.eupixel.sr
import net.eupixel.util.Config

class AutoScaler {
    suspend fun start() {
        val base = getData("lobby_values", "name", "base", "data")
            ?.toInt()?: 1
        Config.lobbyImage = getData("lobby_values", "name", "image","data")
            ?: "anton691/lobby:latest"
        repeat(base) {
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