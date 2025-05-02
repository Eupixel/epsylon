package dev.aquestry.core

import dev.aquestry.sm
import dev.aquestry.su

object ShutdownManager {
    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            sm.stop()
            su.getServers().forEach {
                try {
                    su.deleteServer(it.id)
                } catch (_: Exception) {}
            }
        })
    }
}