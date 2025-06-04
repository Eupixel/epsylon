package net.eupixel.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.eupixel.sr
import net.eupixel.su

class AutoDeleter {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val deleteCounters = mutableMapOf<String, Int>()

    fun start() {
        scope.launch {
            while (isActive) {
                sr.getServers().filter{ it.owned }.forEach { server ->
                    val conditionsMet = server.pending.isEmpty() && server.players == 0 && server.type != "lobby"
                    if (conditionsMet) {
                        val count = deleteCounters.getOrDefault(server.id, 0) + 1
                        if (count >= 5) {
                            su.deleteServer(server.id)
                            deleteCounters.remove(server.id)
                        } else {
                            deleteCounters[server.id] = count
                        }
                    } else {
                        deleteCounters.remove(server.id)
                    }
                }
                delay(1000L)
            }
        }
    }
}