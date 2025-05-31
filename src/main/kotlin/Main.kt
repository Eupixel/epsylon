package net.eupixel

import net.eupixel.core.AutoScaler
import net.eupixel.core.Entrypoint
import net.eupixel.core.ServerMonitor
import net.eupixel.core.ServerRegister
import net.eupixel.core.ShutdownTask
import net.eupixel.util.ServerUtil
import kotlinx.coroutines.runBlocking
import net.eupixel.core.DirectusClient
import net.eupixel.core.MessageHandler
import net.eupixel.core.QueueManager
import net.eupixel.util.Config

val sr = ServerRegister()
val sm = ServerMonitor()
val st = ShutdownTask()
val su = ServerUtil()
val au = AutoScaler()
val et = Entrypoint()
val mh = MessageHandler()
val qm = QueueManager()
val co = Config()

fun main() = runBlocking {
    DirectusClient.initFromEnv()
    sm.start()
    st.start()
    su.start()
    au.start()
    et.start()
    mh.start()
    qm.start()
    co.init()
}