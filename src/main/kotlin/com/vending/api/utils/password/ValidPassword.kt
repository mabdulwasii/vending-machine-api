package com.vending.api.utils.password

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PasswordValidatorConstraint::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ValidPassword(
    val message: String = "Password must be 8 or more characters in length.Password must contain 1 or more uppercase characters.Password must contain 1 or more lowercase characters",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)