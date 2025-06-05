package net.eupixel.util

import net.eupixel.core.Messenger
import net.eupixel.sr

object PlayerUtil {
    fun isOnline(player: String) : Boolean {
        sr.getServers().forEach {
            if(Messenger.sendRequest(it.id, "player_online", player) ==  "true") {
                return true
            }
        }
        return false
    }
}