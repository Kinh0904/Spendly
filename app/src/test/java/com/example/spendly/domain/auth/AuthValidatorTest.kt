package com.example.spendly.domain.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun name_rules() {
        assertEquals(ValidationError.NAME_EMPTY, AuthValidator.validateName("   "))
        assertEquals(ValidationError.NAME_TOO_SHORT, AuthValidator.validateName("A"))
        assertNull(AuthValidator.validateName("Nguyễn Minh Kinh"))
        assertEquals("Nguyễn Minh Kinh", AuthValidator.normalizeName("  Nguyễn   Minh  Kinh "))
    }

    @Test
    fun email_rules() {
        assertEquals(ValidationError.EMAIL_EMPTY, AuthValidator.validateEmail(" "))
        assertEquals(ValidationError.EMAIL_INVALID, AuthValidator.validateEmail("kinh@"))
        assertEquals(ValidationError.EMAIL_INVALID, AuthValidator.validateEmail("kinh@example"))
        assertEquals(ValidationError.EMAIL_INVALID, AuthValidator.validateEmail("kinh example@x.com"))
        assertNull(AuthValidator.validateEmail("Kinh.Nguyen+test@Example.com"))
        assertEquals("kinh@example.com", AuthValidator.normalizeEmail("  Kinh@Example.COM "))
    }

    @Test
    fun phone_is_optional_but_must_be_valid_when_given() {
        assertNull(AuthValidator.validateOptionalPhone(""))
        assertNull(AuthValidator.validateOptionalPhone("   "))
        assertNull(AuthValidator.validateOptionalPhone("0901234567"))
        assertNull(AuthValidator.validateOptionalPhone("0901 234 567"))
        assertNull(AuthValidator.validateOptionalPhone("+84901234567"))
        assertNull(AuthValidator.validateOptionalPhone("84901234567"))
        assertEquals(ValidationError.PHONE_INVALID, AuthValidator.validateOptionalPhone("12345"))
        assertEquals(ValidationError.PHONE_INVALID, AuthValidator.validateOptionalPhone("abcdefghij"))
    }

    @Test
    fun phone_normalization() {
        assertEquals("0901234567", AuthValidator.normalizePhone("+84 901 234 567"))
        assertEquals("0901234567", AuthValidator.normalizePhone("84901234567"))
        assertEquals("0901234567", AuthValidator.normalizePhone("(0901) 234-567"))
        assertEquals("0901234567", AuthValidator.normalizePhone("0901.234.567"))
    }

    @Test
    fun password_rules() {
        assertEquals(ValidationError.PASSWORD_EMPTY, AuthValidator.validateNewPassword(""))
        assertEquals(ValidationError.PASSWORD_WEAK, AuthValidator.validateNewPassword("abc123"))
        assertEquals(ValidationError.PASSWORD_WEAK, AuthValidator.validateNewPassword("abcdefgh"))
        assertEquals(ValidationError.PASSWORD_WEAK, AuthValidator.validateNewPassword("12345678"))
        assertEquals(ValidationError.PASSWORD_WEAK, AuthValidator.validateNewPassword("a1".repeat(65)))
        assertNull(AuthValidator.validateNewPassword("matkhau123"))
    }

    @Test
    fun confirm_password_rules() {
        assertEquals(ValidationError.CONFIRM_EMPTY, AuthValidator.validateConfirmPassword("abc12345", ""))
        assertEquals(ValidationError.CONFIRM_MISMATCH, AuthValidator.validateConfirmPassword("abc12345", "abc12346"))
        assertNull(AuthValidator.validateConfirmPassword("abc12345", "abc12345"))
    }

    @Test
    fun identifier_accepts_email_or_phone() {
        assertEquals(ValidationError.IDENTIFIER_EMPTY, AuthValidator.validateIdentifier("  "))
        assertEquals(ValidationError.IDENTIFIER_INVALID, AuthValidator.validateIdentifier("kinh@"))
        assertEquals(ValidationError.IDENTIFIER_INVALID, AuthValidator.validateIdentifier("123"))
        assertNull(AuthValidator.validateIdentifier("kinh@example.com"))
        assertNull(AuthValidator.validateIdentifier("0901234567"))
        assertEquals("kinh@example.com", AuthValidator.normalizeIdentifier(" Kinh@Example.com "))
        assertEquals("0901234567", AuthValidator.normalizeIdentifier("+84 901 234 567"))
        assertTrue(AuthValidator.isEmail("a@b.co"))
        assertFalse(AuthValidator.isPhone("901234567"))
    }
}
