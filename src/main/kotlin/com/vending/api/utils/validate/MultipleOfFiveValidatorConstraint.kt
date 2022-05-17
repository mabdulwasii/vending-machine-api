package com.vending.api.utils.validate

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class MultipleOfFiveValidatorConstraint : ConstraintValidator<MultipleOfFive, Int?> {
    override fun initialize(constraintAnnotation: MultipleOfFive) {}
    override fun isValid(value: Int?, context: ConstraintValidatorContext): Boolean {

        if (value == null || value % 5 == 0){
            return true
        }
        return false
    }
}