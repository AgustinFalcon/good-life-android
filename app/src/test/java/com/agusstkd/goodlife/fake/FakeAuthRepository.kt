package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var loginResult: Result<User> = Result.Success(DEFAULT_USER)
    var registerResult: Result<User> = Result.Success(DEFAULT_USER)
    var logoutResult: Result<Unit> = Result.Success(Unit)
    var loggedIn = false
    var storedUser: User? = DEFAULT_USER

    var loginCallCount = 0
        private set
    var registerCallCount = 0
        private set
    var logoutCallCount = 0
        private set

    override suspend fun login(email: String, password: String): Result<User> {
        loginCallCount++
        return loginResult
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        registerCallCount++
        return registerResult
    }

    override suspend fun logout(): Result<Unit> {
        logoutCallCount++
        return logoutResult
    }

    override suspend fun isLoggedIn(): Boolean = loggedIn

    override suspend fun getCurrentUser(): Result<User> {
        return storedUser?.let { Result.Success(it) }
            ?: Result.Error(Exception("No user"))
    }

    companion object {
        val DEFAULT_USER = User(
            id = "user-1",
            email = "test@goodlife.com",
            name = "Test User",
            profileImageUrl = null
        )
    }
}
