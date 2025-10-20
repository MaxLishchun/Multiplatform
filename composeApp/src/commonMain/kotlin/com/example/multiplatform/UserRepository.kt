package com.example.multiplatform

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
)

class UserRepository(
    private val api: ApiService,
) {
    suspend fun loadUsers(): List<User> = api.get("https://jsonplaceholder.typicode.com/users")
}
