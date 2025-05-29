package net.eupixel.core

import net.eupixel.sr
import kotlinx.coroutines.*
import net.eupixel.util.PingUtil

class ServerMonitor {
    private var job: Job? = null

    fun start() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        job = scope.launch {
            while (isActive) {
                sr.getServers().forEach { server ->
                    val hostname = server.host.takeIf{!server.owned}?: server.id
                    val port = server.port.takeIf{!server.owned}?: 25565
                    val result = PingUtil.ping(hostname, port)
                    val online = result.first
                    server.players = result.second
                    if (server.state != online) {
                        server.state = online
                        println("New server state $online for ${server.id}")
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