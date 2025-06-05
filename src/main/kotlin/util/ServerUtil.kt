package net.eupixel.util

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import net.eupixel.model.Server
import net.eupixel.sr
import kotlinx.coroutines.runBlocking
import net.eupixel.co
import net.eupixel.core.Messenger
import java.time.Duration

class ServerUtil() {
    val defaultDockerHost = if (System.getProperty("os.name").startsWith("Windows")) {
        "npipe:////./pipe/docker_engine"
    } else {
        "unix:///var/run/docker.sock"
    }
    val dockerHost = System.getenv("DOCKER_HOST") ?: defaultDockerHost

    private val config: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost(dockerHost)
        .build()

    private val httpClient: ApacheDockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .maxConnections(50)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build()

    private val client: DockerClient = DockerClientImpl.getInstance(config, httpClient)

    fun start() {
        // pullImage(co.lobbyImage)
    }

    fun pullImage(image: String) {
        client.pullImageCmd(image).start().awaitCompletion()
    }

    @Suppress("DEPRECATION")
    fun createServer(image: String, type: String): Server = runBlocking {
        val exposedPort = ExposedPort.tcp(25565)
        val portBinding = PortBinding(Ports.Binding.bindPort(0), exposedPort)
        val host = System.getenv("HOST") ?: "none"
        val token = System.getenv("TOKEN") ?: "none"
        val response = client.createContainerCmd(image)
            .withEnv("HOST=$host", "TOKEN=$token")
            .withPortBindings(portBinding)
            .withNetworkMode("entrypoint")
            .exec()
            .also { requireNotNull(it.id) }
            .let {
                client.startContainerCmd(it.id).exec()
                it
            }
        val inspect = client.inspectContainerCmd(response.id).exec()
        val hostPort = inspect.networkSettings.ports.bindings[exposedPort]?.firstOrNull()?.hostPortSpec?.toInt()
            ?: error("No host port bound for $exposedPort")
        val shortid = response.id.take(12)
        client.renameContainerCmd(response.id)
            .withName(shortid)
            .exec()
        val server = Server(shortid, type, image, co.entryHost, "none", hostPort, 0, state = false, owned = true, mutableListOf())
        sr.registerServer(server)
        Messenger.registerTarget(shortid, shortid, 2905)
        println("Created Server: type=$type, host=${co.entryHost}, port=$hostPort, id=$shortid")
        server
    }

    fun deleteServer(id: String) {
        val server = sr.getServers().find { it.id == id }
        if (server != null) {
            sr.unregisterServer(server)
            client.stopContainerCmd(id).withTimeout(10).exec()
            client.removeContainerCmd(id).exec()
            Messenger.unregisterTarget(id)
            println("Deleted Server: type=${server.type}, host=${co.entryHost}, port=${server.port}, id=${server.id}")
        }
    }
}