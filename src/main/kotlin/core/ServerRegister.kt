package dev.aquestry.core

import dev.aquestry.model.Server

class ServerRegister {

    private var servers = mutableListOf<Server>()

    fun registerServer(server: Server) {
        servers.add(server)
    }

    fun unregisterServer(server: Server) {
        servers.remove(server)
    }

    fun getServers(): List<Server> {
        return servers
    }
}