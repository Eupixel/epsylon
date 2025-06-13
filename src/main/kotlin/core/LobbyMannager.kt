package net.eupixel.core

import net.eupixel.co
import net.eupixel.vivlib.core.DirectusClient.getData
import net.eupixel.su
import net.eupixel.model.Server
import net.eupixel.sr

class LobbyMannager {
    fun start() {
        val base = getData("lobby_values", "name", "base", "data")
            ?.toInt()?: 1
        repeat(base) {
            su.createServer(co.lobbyImage, "lobby")
        }
    }

    fun getLobby(): Server? {
        return sr.getServers()
            .filter { it.type == "lobby" && it.state }
            .minByOrNull { it.players }
    }
}