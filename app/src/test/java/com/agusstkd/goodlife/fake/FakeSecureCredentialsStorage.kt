package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage

class FakeSecureCredentialsStorage : SecureCredentialsStorage {

    private var email: String? = null
    private var password: String? = null
    private var biometricEnabled = false

    override fun saveCredentials(email: String, password: String) {
        this.email = email
        this.password = password
    }

    override fun getCredentials(): Pair<String, String>? {
        val e = email ?: return null
        val p = password ?: return null
        return Pair(e, p)
    }

    override fun getSavedEmail(): String? = email

    override fun hasCredentials(): Boolean = email != null && password != null

    override fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabled = enabled
    }

    override fun isBiometricEnabled(): Boolean = biometricEnabled

    override fun clearCredentials() {
        email = null
        password = null
        biometricEnabled = false
    }
}
