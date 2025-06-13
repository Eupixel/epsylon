package net.eupixel.core

import net.eupixel.co
import net.eupixel.lm
import net.eupixel.sr
import net.eupixel.vivlib.core.DirectusClient
import net.eupixel.vivlib.core.Messenger
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.network.packet.server.common.TransferPacket
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
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
                event.apply {
                    val lobby = lm.getLobby()
                    if(lobby == null) {
                        event.player.kick("No lobby available!")
                        return@addListener
                    }
                    spawningInstance = instanceContainer
                    println("${player.username} joined and was redirected to ${lobby.host}:${lobby.port}")
                    Messenger.send(lobby.id, "add_whitelist", "${player.uuid}&${player.playerConnection.remoteAddress}&${co.playerTTL}&${Instant.now()}")
                    player.sendPacket(TransferPacket(lobby.host, lobby.port))
                }
        }

        globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
            var count = 0
            sr.getServers().forEach { server ->
                count += server.players
            }
            event.responseData.online = count
            event.responseData.maxPlayer = 787
            event.responseData.description = motd
            event.responseData.favicon = favicon
        }

        minecraftServer.start("0.0.0.0", 25565)
    }
}