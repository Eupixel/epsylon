package net.eupixel.core

import net.eupixel.lm
import net.eupixel.qm
import net.eupixel.su

class MessageHandler {
    fun start() {
        Messenger.bind("0.0.0.0", 2905)
        Messenger.addListener("queue_join_request", this::queueJoinRequest)
        Messenger.addListener("queue_leave_request", this::queueLeaveRequest)
        Messenger.addListener("lobby", this::lobby)
        Messenger.addListener("rmme", this::rmme)
        println("MessageHandler is now running!")
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

    fun lobby(player: String) {
        val lobby = lm.getLobby()
        if(lobby != null) {
            Messenger.broadcast("transfer", "$player?${lobby.host}&${lobby.port}")
        }
    }

    fun rmme(msg: String) {
        su.deleteServer(msg)
    }
}