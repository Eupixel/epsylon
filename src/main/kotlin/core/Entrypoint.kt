package dev.aquestry.core

import dev.aquestry.au
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.network.packet.server.common.TransferPacket

class Entrypoint {
    fun start() {
        val motd = MiniMessage.miniMessage().deserialize(System.getenv("MOTD")?: "<blue>entrypoint</blue>")
        val favicon = System.getenv("FAVICON")
        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            au.getLobby().let { lobby ->
                event.apply {
                    spawningInstance = instanceContainer
                    println("${player.username} joined to ${lobby.host}:${lobby.port}")
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