package dev.aquestry.core

import dev.aquestry.au
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.network.packet.server.common.TransferPacket

class Entrypoint {
    fun start() {
        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            val target = au.getLobby()
            val port = au.getLobby().port
            var host = "0"
            if(target.host == "host" && event.player.playerConnection.serverAddress != null) {
                host = event.player.playerConnection.serverAddress.toString()
            }
            event.spawningInstance = instanceContainer
            println("${event.player.username} joined to $host:$port")
            event.player.sendPacket(TransferPacket(host, port))
        }

        globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
            event.responseData.online = 0
            event.responseData.maxPlayer = 787
            event.responseData.description = MiniMessage.miniMessage().deserialize("<blue>entrypoint</blue>")
        }

        minecraftServer.start("0.0.0.0", 25565)
    }
}