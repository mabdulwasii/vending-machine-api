package com.vending.api.utils.validate

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [MultipleOfFiveValidatorConstraint::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MultipleOfFive(
    val message: String = "Value must be multiple of five",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)