package dev.aquestry

import dev.aquestry.core.AutoScaler
import dev.aquestry.core.Entrypoint
import dev.aquestry.core.ServerMonitor
import dev.aquestry.core.ServerRegister
import dev.aquestry.core.ShutdownTask
import dev.aquestry.util.ServerUtil
import kotlinx.coroutines.runBlocking

val sr = ServerRegister()
val sm = ServerMonitor()
val st = ShutdownTask()
val su = ServerUtil()
val au = AutoScaler()
val et = Entrypoint()

fun main() = runBlocking {
    sm.start()
    st.start()
    su.start()
    au.start()
    et.start()
}