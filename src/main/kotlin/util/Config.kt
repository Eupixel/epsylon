package net.eupixel.util

import net.eupixel.core.DirectusClient.listItems
import net.eupixel.core.DirectusClient.getData
import net.eupixel.core.DirectusClient.getFields
import net.eupixel.model.Gamemode
import net.eupixel.qm
import net.eupixel.su

class Config {
    var lobbyImage: String = "anton691/lobby:latest"
    var maxPlayersLobby: Int = 20
    val entryHost = System.getenv("ENTRY_HOST") ?: "localhost"

    fun init() {
        lobbyImage = getData("lobby_values", "name", "image", "data")?: "anton691/lobby:latest"
        maxPlayersLobby = getData("lobby_values", "name", "max_players", "data")?.toInt() ?: 20
        val modesNames = listItems("gamemodes", "name")
        for (mode in modesNames) {
            val playerCounts = mutableSetOf<String>()
            val friendlyName = getData("gamemodes", "name", mode, "friendly_name").toString()
            val image = getData("gamemodes", "name", mode, "image").toString()
            getFields("gamemodes", "name", mode, "player_counts").forEach {
                playerCounts.add(it.asText())
            }
            qm.queues.add(Gamemode(mode, friendlyName, image, playerCounts.toMutableList(), ArrayList()))
            su.pullImage(image)
        }
        qm.queues.forEach {
            println("Registered Gamemode: name=${it.name}, friendly_name=${it.friendlyName}, image=${it.image}, player_counts=${it.playerCounts.toList()}")
        }
    }
}