package net.eupixel.core

import net.eupixel.sr
import kotlinx.coroutines.*
import net.eupixel.co
import net.eupixel.vivlib.util.PingUtil
import net.eupixel.vivlib.core.Messenger
import java.time.Instant

class ServerMonitor {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        scope.launch {
            while (isActive) {
                sr.getServers().forEach { server ->
                    val hostname = server.host.takeIf { !server.owned } ?: server.id
                    val port = server.port.takeIf { !server.owned } ?: 25565
                    val result = PingUtil.ping(hostname, port)
                    val online = result.first
                    server.players = result.second
                    if(server.state != online) {
                        server.state = online
                        println("New server state $online for ${server.id}")
                        if(server.state) {
                            val pendingCopy = server.pending.toList()
                            pendingCopy.forEach {
                                Messenger.broadcast("queue_leave", it)
                                Messenger.send(server.id, "add_whitelist", "${it}&${co.playerTTL}&${Instant.now()}")
                                Messenger.broadcast("transfer", "$it?${server.host}&${server.port}")
                            }
                            server.pending.clear()
                        }
                    }
                }
                delay(1000L)
            }
        }
    }
}