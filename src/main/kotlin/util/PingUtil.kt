package net.eupixel.util

import net.lenni0451.mcping.MCPing

object PingUtil {

    private val pingBuilder = MCPing.pingModern().timeout(1000, 1000)

    fun isOnline(host: String, port: Int = 25565): Boolean {
        return try {
            pingBuilder.address(host, port).getSync()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getPlayerCount(host: String, port: Int = 25565): Int {
        return try {
            val response = pingBuilder.address(host, port).getSync()
            response.players.online
        } catch (_: Exception) {
            0
        }
    }
}