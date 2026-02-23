package com.agusstkd.goodlife.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * Request para el endpoint POST /api/v1/register
 *
 * ## Validaciones del backend:
 * - Username: 3-50 caracteres, no vacío
 * - Email: formato válido, no vacío
 * - Password: mínimo 3 caracteres, no vacío
 *
 * ## ¿Las validaciones del backend son "estúpidas"?
 * **NO.** Las validaciones del backend SON NECESARIAS por seguridad.
 *
 * ### Responsabilidades correctas:
 *
 * #### Frontend (tu responsabilidad):
 * - Validar ANTES de hacer el request (usando [ValidateUserNameUseCase], etc.)
 * - Mostrar errores de validación en la UI (campo rojo, mensaje descriptivo)
 * - Evitar requests innecesarios al backend
 * - Mejorar UX (feedback instantáneo sin esperar red)
 *
 * #### Backend (responsabilidad del servidor):
 * - Validar SIEMPRE los datos recibidos (nunca confiar en el cliente)
 * - Proteger contra ataques (modificación de requests, Postman, curl, etc.)
 * - Mantener integridad de la base de datos
 * - Devolver códigos HTTP claros (400 para validación, no 500)
 *
 * ### Flujo correcto:
 * ```
 * Usuario escribe "ab" en username
 *     ↓
 * ValidateUserNameUseCase → ValidationResult.Invalid("Mínimo 3 caracteres")
 *     ↓
 * UI muestra campo rojo con mensaje
 *     ↓
 * Usuario NO puede hacer click en "Registrarse" (botón deshabilitado)
 *     ↓
 * NUNCA llega al backend si los datos son inválidos
 * ```
 *
 * ### ¿Qué pasa si alguien hace trampa?
 * Si un atacante modifica el request con Postman y manda username="a",
 * el backend responde:
 * ```json
 * {
 *   "code": 400,
 *   "message": "Username debe tener entre 3 y 50 caracteres"
 * }
 * ```
 * Y el frontend muestra el error (ApiException.BadRequestException).
 *
 * ## Conclusión:
 * Las validaciones del backend NO arruinan el request.
 * El problema está en el frontend si no validás ANTES de hacer el request.
 *
 * @see com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase
 * @see com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
 * @see com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
 */
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
