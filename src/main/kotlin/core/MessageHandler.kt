package net.eupixel.core

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
        Messenger.broadcast("transfer", "$username?hypixel.net&25565")
    }

    fun global(channel: String, msg: String) {
        println("global:$channel:$msg")
    }
}