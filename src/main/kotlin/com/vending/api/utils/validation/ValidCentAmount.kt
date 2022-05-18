package com.vending.api.utils.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [CentValidatorConstraint::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidCentAmount(
    val message: String = "Cent must be one of 5, 10, 20, 50 or 100",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)