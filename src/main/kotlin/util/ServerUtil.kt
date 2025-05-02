package dev.aquestry.util

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import dev.aquestry.model.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ServerSocket
import java.time.Duration

class ServerUtil(
    dockerUri: String,
    private val minPort: Int,
    private val maxPort: Int
) {

    private val config: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost(dockerUri)
        .build()

    private val httpClient: ApacheDockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .maxConnections(50)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build()

    private val client: DockerClient = DockerClientImpl.getInstance(config, httpClient)
    private val servers = mutableListOf<Server>()

    suspend fun createServer(image: String, type: String): Server = withContext(Dispatchers.IO) {
        client.pullImageCmd(image)
            .start()
            .awaitCompletion()

        val exposedPort = ExposedPort.tcp(25565)
        val port = findFreePort()
        val binding = Ports.Binding.bindPort(port)
        val portBinding = PortBinding(binding, exposedPort)

        val response: CreateContainerResponse = client.createContainerCmd(image)
            .withEnv(
                "EULA=TRUE",
                "MOTD=$image:$port",
                "CUSTOM_SERVER_PROPERTIES=accepts-transfers=true"
            )
            .withExposedPorts(exposedPort)
            .withPortBindings(portBinding)
            .exec()

        val id = requireNotNull(response.id)
        client.startContainerCmd(id).exec()

        val server = Server(id, type, image, "0.0.0.0", port, false)
        synchronized(this@ServerUtil) {
            servers.add(server)
        }
        println("Created Server: type=$type, port=$port, id=$id")
        server
    }

    fun getServers(): List<Server> = servers.toList()

    fun deleteServer(id: String) {
        client.stopContainerCmd(id).withTimeout(10).exec()
        client.removeContainerCmd(id).exec()
        servers.removeAll { it.id == id }
    }

    fun getType(type: String): Server {
        return servers.first { it.type == type }
    }

    fun findFreePort(): Int {
        (minPort..maxPort).forEach { port ->
            try {
                ServerSocket(port).use { return port }
            } catch (_: IOException) {}
        }
        return 0
    }
}