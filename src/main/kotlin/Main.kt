package dev.aquestry

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.InstanceManager
import net.minestom.server.network.packet.server.common.TransferPacket

fun main() {
    val minecraftServer: MinecraftServer = MinecraftServer.init()
    val instanceManager: InstanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer: InstanceContainer = instanceManager.createInstanceContainer()
    val globalEventHandler: GlobalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        event.player.sendPacket(TransferPacket("gommehd.net", 25565))
        event.spawningInstance = instanceContainer
    }

    globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
        event.responseData.online = 0
        event.responseData.maxPlayer = 787
        event.responseData.description = MiniMessage.miniMessage().deserialize("<rainbow>entrypoint</rainbow>")
    }

    minecraftServer.start("0.0.0.0", 25565)
}