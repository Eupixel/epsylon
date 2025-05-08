package dev.aquestry.util

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import dev.aquestry.config.Config
import dev.aquestry.model.Server
import dev.aquestry.sr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

class ServerUtil() {

    val entryHost = System.getenv("ENTRY_HOST") ?: "localhost"

    val dockerHost = System.getenv("DOCKER_HOST") ?: Config.dockerHost

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
        client.pullImageCmd(Config.lobbyImage).start().awaitCompletion()
    }

    @Suppress("DEPRECATION")
    suspend fun createServer(image: String, type: String): Server = withContext(Dispatchers.IO) {
        val exposedPort = ExposedPort.tcp(25565)
        val portBinding = PortBinding(Ports.Binding.bindPort(0), exposedPort)

        val response = client.createContainerCmd(image)
            .withEnv(
                "EULA=TRUE",
                "CUSTOM_SERVER_PROPERTIES=accepts-transfers=true"
            )
            .withExposedPorts(exposedPort)
            .withPortBindings(portBinding)
            .exec()
            .also { requireNotNull(it.id) }
            .let {
                client.startContainerCmd(it.id).exec()
                it
            }

        val inspect = client.inspectContainerCmd(response.id).exec()
        val hostPort = inspect.networkSettings
            .ports
            .bindings[exposedPort]
            ?.firstOrNull()
            ?.hostPortSpec
            ?.toInt()
            ?: error("No host port bound for $exposedPort")

        val server = Server(response.id, type, image, entryHost, hostPort,0,false)
        sr.registerServer(server)
        println("Created Server: type=$type, host=$entryHost, port=$hostPort, id=${response.id}")
        server
    }

    fun deleteServer(id: String) {
        client.stopContainerCmd(id).withTimeout(10).exec()
        client.removeContainerCmd(id).exec()
    }
}