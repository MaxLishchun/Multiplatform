package com.example.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService {
    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    internal suspend inline fun <reified T> get(url: String): T = client.get(url).body()

    private suspend inline fun <reified Req, reified Res> post(
        url: String,
        body: Req,
    ): Res = client.post(url) { setBody(body) }.body()
}
