package dev.aquestry.core

import dev.aquestry.sm
import dev.aquestry.sr
import dev.aquestry.su

class ShutdownTask {
    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            sm.stop()
            sr.getServers().forEach {
                try {
                    su.deleteServer(it.id)
                } catch (_: Exception) {}
            }
        })
    }
}