package net.eupixel.core

import kotlinx.coroutines.runBlocking
import net.eupixel.co
import net.eupixel.su
import net.minestom.server.MinecraftServer

class MessageHandler {
    fun start() {
        Messenger.bind("0.0.0.0", 2905)
        Messenger.addGlobalListener(this::global)
        Messenger.addListener("queue_left", this::queueLeft)
        Messenger.addListener("queue_joined", this::queueJoined)
        println("MessageHandler is now running!")
    }

    fun queueLeft(msg: String) {
        println("queue_left:$msg")
    }

    fun queueJoined(msg: String) {
        println("queue_joined:$msg")
        val username = msg.split("&")[0]
        println("DEBUG 1: $username")
        co.gamemodes.forEach {
            if(it.friendlyName == msg.split("&")[1]) {
                println("DEBUG 2: $username")
                if(it.playerCounts.contains("1x1")) {
                    println("DEBUG 3: $username")
                    Thread {
                        runBlocking {
                            println("DEBUG 4")
                            val server = su.createServer(it.image, it.name)
                            server.pending.add(username)
                        }
                    }.start()
                } else {
                    // TODO
                }
            }
        }
    }

    fun global(channel: String, msg: String) {
        println("global:$channel:$msg")
    }
}