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
import java.time.Duration

data class Server(val id: String, val port: Int)

class MinecraftServerUtil(
    dockerUri: String = "npipe:////./pipe/docker_engine",
    private val image: String = "itzg/minecraft-server:latest"
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

    fun createServer(name: String, externalPort: Int): String {
        client.pullImageCmd(image).start().awaitCompletion()
        val exposedPort = ExposedPort.tcp(25565)
        val binding = Ports.Binding.bindPort(externalPort)
        val portBinding = PortBinding(binding, exposedPort)
        val response: CreateContainerResponse = client.createContainerCmd(image)
            .withName(name)
            .withEnv("EULA=TRUE")
            .withExposedPorts(exposedPort)
            .withPortBindings(portBinding)
            .exec()
        val id = requireNotNull(response.id) { "Failed to create container: id was null" }
        client.startContainerCmd(id).exec()
        servers.add(Server(id, externalPort))
        return id
    }

    fun getServers(): List<Server> {
        return servers.toList()
    }

    fun deleteServer(id: String) {
        client.stopContainerCmd(id).withTimeout(10).exec()
        client.removeContainerCmd(id).exec()
        servers.removeAll { it.id == id }
    }

    fun startServer(id: String) {
        client.startContainerCmd(id).exec()
    }

    fun stopServer(id: String) {
        client.stopContainerCmd(id).withTimeout(10).exec()
    }

    fun getStatus(id: String): String {
        val info = client.inspectContainerCmd(id).exec()
        val status = info.state?.status
        return requireNotNull(status) { "Failed to get status for container: $id" }
    }
}