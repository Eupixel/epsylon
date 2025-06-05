package net.eupixel.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.eupixel.model.Gamemode
import net.eupixel.su
import net.eupixel.util.PlayerUtil

class QueueManager {
    val queues = mutableListOf<Gamemode>()
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        scope.launch {
            while (isActive) {
                proccessQueue()
                delay(1000)
            }
        }
    }

    fun proccessQueue() {
        queues.forEach outer@ { gamemode ->
            gamemode.queued.forEach {
                if(!PlayerUtil.isOnline(it)) {
                    gamemode.queued.remove(it)
                    return@outer
                }
            }
            val capacities = gamemode.playerCounts.mapNotNull { countStr ->
                val parts = countStr.split("x")
                val teams = parts.getOrNull(0)?.toIntOrNull()
                val perTeam = parts.getOrNull(1)?.toIntOrNull()
                if (teams != null && perTeam != null) teams * perTeam else null
            }.sortedDescending()
            capacities.forEach { capacity ->
                while (gamemode.queued.size >= capacity) {
                    val server = su.createServer(gamemode.image, gamemode.name)
                    server.queue = gamemode.name
                    repeat(capacity) {
                        val player = gamemode.queued.removeAt(0)
                        server.pending.add(player)
                    }
                }
            }
        }
    }

    fun addPlayer(player: String, gamemode: String) {
        queues.forEach {
            if(it.friendlyName == gamemode) {
                println("Added Player to Queue: username=${player}, name=${it.name}")
                it.queued.add(player)
            }
        }
    }

    fun removePlayer(player: String) {
        queues.forEach {
            if(it.queued.contains(player)) {
                println("Removed Player from Queue: username=${player}, name=${it.name}")
                it.queued.remove(player)
            }
        }
    }

    fun inQueue(player: String): Boolean = queues.any { it.queued.contains(player) }
}