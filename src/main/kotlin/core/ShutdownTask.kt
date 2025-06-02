package net.eupixel.core

import net.eupixel.sr
import net.eupixel.su

class ShutdownTask {
    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            sr.getServers().forEach {
                try {
                    su.deleteServer(it.id)
                } catch (_: Exception) {}
            }
        })
    }
}