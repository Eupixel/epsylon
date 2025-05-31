package net.eupixel.util

import net.eupixel.core.DirectusClient.listItems
import net.eupixel.core.DirectusClient.getData
import net.eupixel.core.DirectusClient.getFields
import net.eupixel.model.Gamemode

class Config {
    var lobbyImage: String = "anton691/lobby:latest"
    val entryHost = System.getenv("ENTRY_HOST") ?: "localhost"
    val gamemodes = mutableSetOf<Gamemode>()

    fun init() {
        val modesNames = listItems("gamemodes", "name")
        for (mode in modesNames) {
            val playerCounts = mutableSetOf<String>()
            val friendlyName = getData("gamemodes", "name", mode, "friendly_name").toString()
            val image = getData("gamemodes", "name", mode, "image").toString()
            getFields("gamemodes", "name", mode, "player_counts").forEach {
                playerCounts.add(it.asText())
            }
            gamemodes.add(Gamemode(mode, friendlyName, image, playerCounts.toTypedArray()))
        }
        gamemodes.forEach {
            println("Registered Gamemode: name=${it.name}, friendly_name=${it.friendlyName}, image=${it.image}, player_counts=${it.playerCounts.toList()}")
        }
    }
}