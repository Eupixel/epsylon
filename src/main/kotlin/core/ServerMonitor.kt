package net.eupixel.core

import net.eupixel.sr
import net.eupixel.util.PingUtil
import kotlinx.coroutines.*

class ServerMonitor {
    private var job: Job? = null

    fun start() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        job = scope.launch {
            while (isActive) {
                sr.getServers().forEach { server ->
                    val hostName = server.host.takeIf { it != "host" } ?: "localhost"
                    val port = server.port
                    val online = PingUtil.isOnline(hostName, port)
                    if (server.state != online) {
                        server.state = online
                        println("New server state $online for ${server.id}")
                    }
                    if (online) {
                        server.players = PingUtil.getPlayerCount(hostName, port)
                    } else {
                        server.players = 0
                    }
                }
                delay(1_000L)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}