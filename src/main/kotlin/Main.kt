package net.eupixel

import net.eupixel.core.AutoScaler
import net.eupixel.core.Entrypoint
import net.eupixel.core.ServerMonitor
import net.eupixel.core.ServerRegister
import net.eupixel.core.ShutdownTask
import net.eupixel.util.ServerUtil
import kotlinx.coroutines.runBlocking
import net.eupixel.core.AutoDeleter
import net.eupixel.core.DirectusClient
import net.eupixel.core.MessageHandler
import net.eupixel.core.QueueManager
import net.eupixel.util.Config

val sr = ServerRegister()
val sm = ServerMonitor()
val st = ShutdownTask()
val su = ServerUtil()
val lm = AutoScaler()
val et = Entrypoint()
val mh = MessageHandler()
val ad = AutoDeleter()
val qm = QueueManager()
val co = Config()

fun main() = runBlocking {
    DirectusClient.initFromEnv()
    co.init()
    sm.start()
    st.start()
    su.start()
    lm.start()
    et.start()
    mh.start()
    ad.start()
    qm.start()
}