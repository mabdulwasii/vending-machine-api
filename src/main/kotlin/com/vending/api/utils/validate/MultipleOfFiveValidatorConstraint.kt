package com.vending.api.utils.validate

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class MultipleOfFiveValidatorConstraint : ConstraintValidator<MultiplesOfFive, Int> {
    override fun initialize(constraintAnnotation: MultiplesOfFive) {}
    override fun isValid(value: Int, context: ConstraintValidatorContext): Boolean {

        if (value % 5 == 0){
            return true
        }
        return false
    }
}