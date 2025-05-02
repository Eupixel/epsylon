package dev.aquestry.core

import dev.aquestry.au
import dev.aquestry.su
import dev.aquestry.util.PingUtil
import kotlinx.coroutines.*

class ServerMonitor(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private var job: Job? = null

    fun start() {
        job = scope.launch {
            while (isActive) {
                su.getServers().forEach {
                    val port = au.getLobby().port
                    var host = it.host
                    if(it.host == "host") {
                        host = "localhost"
                    }
                    val old = it.state
                    val new = PingUtil.isOnline(host, port)
                    if(old != new) {
                        it.state = new
                        println("New server state $new for ${it.id}")
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