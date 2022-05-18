package com.vending.api.utils.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class CentValidatorConstraint : ConstraintValidator<ValidCentAmount, Int> {
    override fun initialize(constraintAnnotation: ValidCentAmount) {}
    override fun isValid(value: Int, context: ConstraintValidatorContext): Boolean {
        val validCent = listOf(5, 10, 20, 50, 100)

        if (validCent.contains(value)){
            return true
        }
        return false
    }
}