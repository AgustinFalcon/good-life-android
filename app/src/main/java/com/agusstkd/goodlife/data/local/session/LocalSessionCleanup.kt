package com.agusstkd.goodlife.data.local.session

import com.agusstkd.goodlife.core.session.SessionCleanup
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.local.dao.DailyDao
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * Borra tokens, credenciales biométricas, usuario Room y cache Daily.
 * Lo usan el logout explícito, el Authenticator al expirar, y el login
 * para no heredar PII de otra cuenta si el expiry falló a medias.
 */
class LocalSessionCleanup(
    private val tokenManager: TokenManager,
    private val secureCredentialsStorage: SecureCredentialsStorage,
    private val userDao: UserDao,
    private val dailyDao: DailyDao,
) : SessionCleanup {
    override suspend fun clear() {
        runSessionClearSteps(
            { tokenManager.clearTokens() },
            { secureCredentialsStorage.clearCredentials() },
            { dailyDao.clearCache() },
            { userDao.deleteUser() },
        )?.let { throw it }
    }

    override suspend fun clearBestEffort() = withContext(NonCancellable) {
        try {
            clear()
        } catch (_: Exception) {
            // Nunca enmascara el fallo original de logout o refresh.
        }
    }

    override suspend fun discardLocalAccountBestEffort() = withContext(NonCancellable) {
        runSessionClearSteps(
            { dailyDao.clearCache() },
            { userDao.deleteUser() },
        )
        Unit
    }
}
