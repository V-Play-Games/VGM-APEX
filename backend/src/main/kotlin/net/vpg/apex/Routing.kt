package net.vpg.apex

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vpg.apex.route.historyRoute
import net.vpg.apex.route.loginRoute

fun Application.configureRouting() = routing {
    get("/") {
        call.respondText("The server is running!")
    }

    historyRoute()
    loginRoute()
}

