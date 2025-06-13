package net.eupixel.core

import net.eupixel.co
import net.eupixel.lm
import net.eupixel.qm
import net.eupixel.sr
import net.eupixel.su
import net.eupixel.vivlib.core.Messenger
import java.time.Instant

class MessageHandler {
    fun start() {
        Messenger.bind("0.0.0.0", 2905)
        Messenger.addRequestHandler("lobby_list", ::handleLobbyList)
        Messenger.addListener("queue_join_request", ::queueJoinRequest)
        Messenger.addListener("queue_leave_request", ::queueLeaveRequest)
        Messenger.addListener("lobby", ::lobby)
        Messenger.addListener("rmme", ::rmme)
        println("MessageHandler is now running!")
    }

    private fun handleLobbyList(msg: String = ""): String {
        val servers = sr.getServers().filter { server -> server.state && server.players <= co.maxPlayersLobby && server.type == "lobby" }
        return servers.joinToString("#") { "${it.host}&${it.port}&${it.id}&${it.players}" }
    }

    fun queueJoinRequest(msg: String) {
        val username = msg.split("&")[0]
        val gamemode = msg.split("&")[1]
        if(!qm.inQueue(username)) {
            qm.addPlayer(username, gamemode)
            Messenger.broadcast("queue_join", "$username&$gamemode")
        }
    }

    fun queueLeaveRequest(player: String) {
        if(qm.inQueue(player)) {
            qm.removePlayer(player)
            Messenger.broadcast("queue_leave", player)
        }
    }

    fun lobby(msg: String) {
        val lobby = lm.getLobby()
        if(lobby != null) {
            Messenger.send(lobby.id, "add_whitelist", "$msg&${co.playerTTL}&${Instant.now()}")
            Messenger.broadcast("transfer", "$msg?${lobby.host}&${lobby.port}")
        }
    }

    fun rmme(msg: String) {
        su.deleteServer(msg)
    }
}