package net.eupixel.util

import net.eupixel.vivlib.core.Messenger
import net.eupixel.sr

object PlayerUtil {
    fun isOnline(uuid: String) : Boolean {
        sr.getServers().forEach {
            if(Messenger.sendRequest(it.id, "player_online", uuid) ==  "true") {
                return true
            }
        }
        return false
    }
}