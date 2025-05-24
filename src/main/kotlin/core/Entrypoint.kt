package net.eupixel.core

import net.eupixel.au
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.network.packet.server.common.TransferPacket
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64

class Entrypoint {
    fun start() {
        DirectusClient.downloadFile("icons", "name", "server", "icon", "icon.png")
        val rawMOTD = DirectusClient.getData("global_values", "name", "motd", "data").toString()
        val motd = MiniMessage.miniMessage().deserialize(rawMOTD)
        val iconPath = Paths.get("icon.png")
        val iconBytes = Files.readAllBytes(iconPath)
        val favicon = "data:image/png;base64," + Base64.getEncoder().encodeToString(iconBytes)
        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            au.getLobby().let { lobby ->
                event.apply {
                    spawningInstance = instanceContainer
                    println("${player.username} joined and was redirected to ${lobby.host}:${lobby.port}")
                    player.sendPacket(TransferPacket(lobby.host, lobby.port))
                }
            }
        }

        globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
            event.responseData.online = 0
            event.responseData.maxPlayer = 787
            event.responseData.description = motd
            event.responseData.favicon = favicon
        }

        minecraftServer.start("0.0.0.0", 25565)
    }
}