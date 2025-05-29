package net.eupixel.core

class MessageHandler {
    fun start() {
        Messenger.bind("0.0.0.0", 2905)
        Messenger.addGlobalListener(this::global)
        Messenger.addListener("queue_left", this::queueLeft)
        Messenger.addListener("queue_joined", this::queueJoined)
    }

    fun queueLeft(msg: String) {
        print("queue_left:$msg")
    }

    fun queueJoined(msg: String) {
        print("queue_joined:$msg")
    }

    fun global(channel: String, msg: String) {
        print("global:$channel:$msg")
    }
}