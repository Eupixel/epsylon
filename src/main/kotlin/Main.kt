package dev.aquestry

import dev.aquestry.config.Config
import dev.aquestry.core.AutoScaler
import dev.aquestry.core.Entrypoint
import dev.aquestry.core.ServerMonitor
import dev.aquestry.core.ShutdownManager
import dev.aquestry.util.ServerUtil

val su = ServerUtil(Config.dockerUri, Config.minPort, Config.maxPort)
val au = AutoScaler()
val sm = ServerMonitor()

suspend fun main() {
    ShutdownManager.start()
    Entrypoint().start()
    sm.start()
    au.start()
}