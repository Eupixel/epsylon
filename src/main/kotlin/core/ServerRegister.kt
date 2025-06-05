package net.eupixel.core

import net.eupixel.model.Server

class ServerRegister {
    private var servers = mutableListOf<Server>()

    fun registerServer(server: Server) {
        servers.add(server)
    }

    fun unregisterServer(server: Server) {
        servers.remove(server)
    }

    fun getServers(): List<Server> {
        val snapshot = servers.toList()
        return snapshot
    }
}