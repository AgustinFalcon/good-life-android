package com.agusstkd.goodlife.domain.usecase.validation

import com.agusstkd.goodlife.core.datetime.language.Spanish
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUseCasesTest {

    private val language = Spanish

    @Test
    fun `email validation accepts usernames and valid email addresses`() {
        val validate = ValidateEmailUseCase(language)

        assertTrue(validate("agus").isValid)
        assertTrue(validate("agus@test.com").isValid)
    }

    @Test
    fun `email validation rejects blank short and malformed email values`() {
        val validate = ValidateEmailUseCase(language)

        assertFalse(validate("").isValid)
        assertFalse(validate("ab").isValid)
        assertFalse(validate("agus@").isValid)
    }

    @Test
    fun `password validation accepts basic and strict valid passwords`() {
        val validate = ValidatePasswordUseCase(language)

        assertTrue(validate("abcd").isValid)
        assertTrue(validate.validateStrict("Abcd123!").isValid)
    }

    @Test
    fun `password validation rejects each strict rule independently`() {
        val validate = ValidatePasswordUseCase(language)

        assertFalse(validate("").isValid)
        assertFalse(validate("abc").isValid)
        assertFalse(validate("a".repeat(51)).isValid)
        assertFalse(validate.validateStrict("abcd123!").isValid)
        assertFalse(validate.validateStrict("Abcdefg!").isValid)
        assertFalse(validate.validateStrict("Abcd1234").isValid)
    }

    @Test
    fun `name and password match validations cover success and failure`() {
        val validateFullName = ValidateFullNameUseCase(language)
        val validateUserName = ValidateUserNameUseCase(language)
        val validateMatch = ValidatePasswordMatchUseCase(language)

        assertTrue(validateFullName("Agustin Falcon").isValid)
        assertFalse(validateFullName("A").isValid)
        assertTrue(validateUserName("agustin").isValid)
        assertFalse(validateUserName("ag").isValid)
        assertTrue(validateMatch("secret", "secret").isValid)
        assertFalse(validateMatch("secret", "different").isValid)
    }
}
