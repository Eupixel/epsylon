package net.eupixel.core

import net.eupixel.au
import net.eupixel.qm
import net.eupixel.su

class MessageHandler {
    fun start() {
        Messenger.bind("0.0.0.0", 2905)
        Messenger.addListener("queue_join", this::queueJoin)
        Messenger.addListener("lobby", this::lobby)
        Messenger.addListener("rmme", this::rmme)
        println("MessageHandler is now running!")
    }


    fun queueJoin(msg: String) {
        val username = msg.split("&")[0]
        val gamemode = msg.split("&")[1]
        qm.addPlayer(username, gamemode)
    }


    fun lobby(msg: String) {
        val lobby = au.getLobby()
        if(lobby != null) {
            Messenger.broadcast("transfer", "$msg?${lobby.host}&${lobby.port}")
        }
    }

    fun rmme(msg: String) {
        su.deleteServer(msg)
    }
}