package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.exception.NoSessionException
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {

    var loginResult: Result<User> = Result.Success(DEFAULT_USER)
    var loginHandler: (suspend (String, String) -> Result<User>)? = null
    var registerResult: Result<User> = Result.Success(DEFAULT_USER)
    var logoutResult: Result<Unit> = Result.Success(Unit)
    var loggedIn = false
    val storedUser = MutableStateFlow<User?>(DEFAULT_USER)

    var loginCallCount = 0
        private set
    var registerCallCount = 0
        private set
    var logoutCallCount = 0
        private set

    override suspend fun login(email: String, password: String): Result<User> {
        loginCallCount++
        return loginHandler?.invoke(email, password) ?: loginResult
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
        return storedUser.value?.let { Result.Success(it) }
            ?: Result.Error(Exception("No user"))
    }

    override fun observeCurrentUser(): Flow<User?> = storedUser

    override suspend fun updateProfileImageUrl(url: String): Result<Unit> {
        val current = storedUser.value ?: return Result.Error(NoSessionException())
        storedUser.value = current.copy(profileImageUrl = url)
        return Result.Success(Unit)
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
