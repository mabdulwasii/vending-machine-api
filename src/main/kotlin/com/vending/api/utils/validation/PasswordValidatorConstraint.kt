package com.vending.api.utils.validation

import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.LengthRule
import org.passay.PasswordData
import org.passay.PasswordValidator
import org.passay.WhitespaceRule
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PasswordValidatorConstraint : ConstraintValidator<ValidPassword?, String> {
    override fun initialize(constraintAnnotation: ValidPassword?) {}
    override fun isValid(password: String, context: ConstraintValidatorContext): Boolean {
        val validator = PasswordValidator(
            listOf( // at least 8 characters
                LengthRule(8, 255),  // at least one upper-case character
                CharacterRule(EnglishCharacterData.UpperCase, 1),  // at least one lower-case character
                CharacterRule(EnglishCharacterData.LowerCase, 1),  // at least one digit character
                CharacterRule(EnglishCharacterData.Digit, 1),  // at least one symbol (special character)
                CharacterRule(EnglishCharacterData.Special, 1),  // no whitespace
                WhitespaceRule() // no common passwords
                //            dictionaryRule
            )
        )
        val result = validator.validate(PasswordData(password))
        if (result.isValid) {
            return true
        }
        val messages = validator.getMessages(result)
        val messageTemplate = java.lang.String.join(",", messages)
        context.buildConstraintViolationWithTemplate(messageTemplate)
            .addConstraintViolation()
            .disableDefaultConstraintViolation()
        return false
    }
}