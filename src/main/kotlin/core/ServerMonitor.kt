package dev.aquestry.core

import dev.aquestry.au
import dev.aquestry.su
import dev.aquestry.util.PingUtil
import kotlinx.coroutines.*

class ServerMonitor() {

    private var job: Job? = null

    fun start() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        job = scope.launch {
            while (isActive) {
                su.getServers().forEach {
                    val port = au.getLobby().port
                    it.apply {
                        val hostName = host.takeIf { it != "host" } ?: "localhost"
                        PingUtil.isOnline(hostName, port).also { online ->
                            if (state != online) {
                                state = online
                                println("New server state $online for $id")
                            }
                            if (online) players = PingUtil.getPlayerCount(hostName, port)
                        }
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