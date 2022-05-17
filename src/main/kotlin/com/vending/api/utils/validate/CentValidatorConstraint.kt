package com.vending.api.utils.validate

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class CentValidatorConstraint : ConstraintValidator<ValidCent, Int> {
    override fun initialize(constraintAnnotation: ValidCent) {}
    override fun isValid(value: Int, context: ConstraintValidatorContext): Boolean {
        val validCent = listOf(5, 10, 20, 50, 100)

        if (validCent.contains(value)){
            return true
        }
        return false
    }
}