package net.vplaygames.apex

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.logging.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import net.vplaygames.apex.route.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.FileInputStream
import java.net.ServerSocket

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        port = args.firstOrNull()?.toIntOrNull() ?: ServerSocket(0).use { it.localPort },
        module = Application::module
    ).start(wait = true)
}

private val logger = LoggerFactory.getLogger("ResponseLogger")
private val ResponseBodyKey = AttributeKey<String>("ResponseBody")

fun Application.module() {
    installContentNegotiation()
    installCallLogging()
    installResponseBodyLogger()

    initializeFirebase()
    configureRouting()
}

private fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

private fun Application.installCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val request = call.request.toLogString()
            "$request -> $status"
        }
    }
}

private fun Application.installResponseBodyLogger() {
    sendPipeline.intercept(ApplicationSendPipeline.Transform) { message ->
        if (message !is OutgoingContent) {
            call.attributes.put(ResponseBodyKey, message.toString())
        }
    }

    intercept(ApplicationCallPipeline.Plugins) {
        proceed()
        val responseBody = call.attributes.getOrNull(ResponseBodyKey)
        if (responseBody != null) {
            logger.info("Response body: $responseBody")
        }
    }
}

fun initializeFirebase() {
    FirebaseApp.initializeApp(
        FirebaseOptions.builder().setCredentials(
            GoogleCredentials.fromStream(FileInputStream("firebase-service-account.json"))
        ).build()
    )
}

fun Application.configureRouting() = routing {
    get("/") { call.respondText("The server is running!") }
    albumRoute()
    historyRoute()
    loginRoute()
    trackRoute()
    searchRoute()
    uploaderRoute()
}
