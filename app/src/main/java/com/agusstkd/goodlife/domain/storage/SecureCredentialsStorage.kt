package com.agusstkd.goodlife.domain.storage

/**
 * Interface para almacenamiento seguro de credenciales.
 *
 * Define el contrato para guardar/recuperar credenciales de forma segura,
 * agnóstico a la plataforma para ser KMP-compatible.
 *
 * ## Implementaciones:
 * - Android: [com.agusstkd.goodlife.platform.storage.AndroidSecureCredentialsStorage]
 *   usando EncryptedSharedPreferences
 * - iOS: (futuro) Usando Keychain Services
 * - Desktop: (futuro) Usando sistema de keyring del OS
 *
 * ## Seguridad:
 * Las implementaciones DEBEN usar almacenamiento encriptado.
 * NUNCA guardar credenciales en texto plano.
 */
interface SecureCredentialsStorage {

    /**
     * Guarda las credenciales de forma segura.
     *
     * @param email Email/usuario
     * @param password Contraseña
     */
    fun saveCredentials(email: String, password: String)

    /**
     * Obtiene las credenciales guardadas.
     *
     * @return Par (email, password) o null si no hay credenciales guardadas
     */
    fun getCredentials(): Pair<String, String>?

    /**
     * Obtiene solo el email guardado (sin password).
     * Útil para autocompletar el campo cuando el usuario cancela la biometría.
     *
     * @return Email guardado o null si no hay
     */
    fun getSavedEmail(): String?

    /**
     * Verifica si hay credenciales guardadas.
     *
     * @return true si hay credenciales almacenadas
     */
    fun hasCredentials(): Boolean

    /**
     * Activa o desactiva el login biométrico.
     *
     * @param enabled true para activar, false para desactivar
     */
    fun setBiometricEnabled(enabled: Boolean)

    /**
     * Verifica si el login biométrico está activado.
     *
     * @return true si está activado
     */
    fun isBiometricEnabled(): Boolean

    /**
     * Limpia todas las credenciales almacenadas.
     *
     * Debe llamarse al hacer logout para limpiar datos sensibles.
     */
    fun clearCredentials()
}
