package com.vending.api.exception.handler

import com.vending.api.dto.ApiResponse
import com.vending.api.exception.GenericException
import com.vending.api.exception.InvalidRoleException
import com.vending.api.exception.InvalidUserNameException
import com.vending.api.exception.PasswordMismatchException
import com.vending.api.exception.TokenRefreshExpiredException
import com.vending.api.exception.UserNameExistsException
import com.vending.api.utils.ConstantUtils.API_TIMEOUT
import com.vending.api.utils.ConstantUtils.BAD_CREDENTIALS
import com.vending.api.utils.ConstantUtils.CONNECTION_ERROR
import com.vending.api.utils.ConstantUtils.DATABASE_ERROR
import com.vending.api.utils.ConstantUtils.ENTITY_EXISTS
import com.vending.api.utils.ConstantUtils.ERROR_PLEASE_TRY_AGAIN
import com.vending.api.utils.ConstantUtils.ERROR_WRITING_JSON
import com.vending.api.utils.ConstantUtils.INSUFFICIENT_AUTHENTICATION
import com.vending.api.utils.ConstantUtils.INVALID_DATA
import com.vending.api.utils.ConstantUtils.INVALID_NUMBER_FORMAT
import com.vending.api.utils.ConstantUtils.INVALID_PARAMETERS
import com.vending.api.utils.ConstantUtils.INVALID_REQUEST_PAYLOAD
import com.vending.api.utils.ConstantUtils.MEDIA_NOT_SUPPORT
import com.vending.api.utils.ConstantUtils.NULL_ERROR
import com.vending.api.utils.ConstantUtils.PARAMETER_MISSING
import com.vending.api.utils.ConstantUtils.USERNAME_NOT_FOUND
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.function.Consumer
import java.util.stream.Collectors
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.ObjectError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityNotFoundException
import javax.persistence.NonUniqueResultException
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@ControllerAdvice
class RestApiExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(javaClass)
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleHttpMessageNotReadable ", ex)
        val errorMessage = INVALID_REQUEST_PAYLOAD
        return buildErrorResponseEntity(
           errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    // Handle handleClientErrorException. Happens when request Entity is unprocessable.
    @ExceptionHandler(HttpClientErrorException::class)
    fun handleClientErrorException(ex: HttpClientErrorException): ResponseEntity<Any> {
        log.error(" handleClientErrorException ", ex)
        val errorMessage = "Request entity unprocessable"
        return buildErrorResponseEntity(
            errorMessage, ex.localizedMessage,
            HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    // Handles IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Any> {
        log.error(" handleIllegalArgumentException ", ex)
        val errorMessage = INVALID_PARAMETERS
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    // Handles ResourceAccessException
    @ExceptionHandler(ResourceAccessException::class)
    fun handleResourceAccessException(ex: ResourceAccessException): ResponseEntity<Any> {
        log.error(" handleResourceAccessException exception ", ex)
        val errorMessage = "Invalid access to resource"
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.UNAUTHORIZED
        )
    }

    // Handles ConnectException
    // Handle ConnectException. Happens when request JSON is malformed.
    @ExceptionHandler(ConnectException::class)
    fun handleConnectException(ex: ConnectException): ResponseEntity<Any> {
        log.error(" handleConnectException ", ex)
        val errorMessage = "Remote connection not found"
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.SERVICE_UNAVAILABLE
        )
    }

    // Handles AccessDeniedException
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<Any> {
        log.error(" handleAccessDeniedException ", ex)
        val errorMessage = "Oops, you do not have access to this resource"
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.UNAUTHORIZED
        )
    }

    // Handles IllegalStateException
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalException(ex: IllegalStateException): ResponseEntity<Any> {
        log.error(" handleIllegalException ", ex)
        val errorMessage = "Malformed JSON request"
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    // Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any> {
        log.error(" handleEntityNotFound ", ex)
        val errorMessage = "Resource object not found"
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.NOT_FOUND
        )
    }

    //Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException, headers: HttpHeaders,
        status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleMissingServletRequestParameter ", ex)
        val errorMessage: String = ex.parameterName + PARAMETER_MISSING
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    //  Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid.
    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders, status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleHttpMediaTypeNotSupported ", ex)
        val builder = StringBuilder()
        builder.append(ex.contentType)
        builder.append(MEDIA_NOT_SUPPORT)
        ex.supportedMediaTypes.forEach(Consumer { t: MediaType? -> builder.append(t).append(", ") })
        return buildErrorResponseEntity(
            
            builder.substring(0, builder.length - 2),
            ex.localizedMessage,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE
        )
    }

    // Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleMethodArgumentNotValid ", ex)
        val errors: MutableList<String> = ArrayList()
        ex.bindingResult.allErrors.forEach(Consumer { objectError: ObjectError ->
            val errorMessage: String = objectError.defaultMessage.toString()
            errors.add(errorMessage)
        })
        return buildErrorResponseEntity(

            java.lang.String.join("\n", errors).replace(",", ""),
            listOf<String>(),
            HttpStatus.BAD_REQUEST
        )
    }

    //Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException
    ): ResponseEntity<Any> {
        log.error(" handleConstraintViolation ", ex)
        val errors: MutableMap<String, String> = HashMap()
        val constraintViolations: Set<ConstraintViolation<*>> = ex.constraintViolations
        constraintViolations.forEach(Consumer { constraintViolation: ConstraintViolation<*> ->
            val message: String = constraintViolation.message
            val messageTemplate: String = constraintViolation.messageTemplate
            errors[messageTemplate] = message
        })
        return buildErrorResponseEntity(
            
            errors.entries
                .stream()
                .map { (key, value): Map.Entry<String, String> -> "$key : $value" }
                .collect(Collectors.joining("\n")),
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST)
    }

    // Handle HttpMessageNotWritableException.
    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleHttpMessageNotWritable ", ex)
        val errorMessage = ERROR_WRITING_JSON
        println("Error writing JSON output ==> Cause " + ex.cause)
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    //Handle NoHandlerFoundException.
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        log.error(" handleHttpMessageNotWritable ", ex)
        val errorMessage = String.format(
            "Could not find the %s method for URL %s",
            ex.httpMethod, ex.requestURL
        )
        return buildErrorResponseEntity(
             errorMessage, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    //Handle MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        log.error("handleMethodArgumentTypeMismatch ", ex)
        val errorMessage = String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            ex.name, ex.value, ex.requiredType
        )
        return buildErrorResponseEntity(
            
            errorMessage,
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    //Handle DataIntegrityViolationException, inspects the cause for different DB causes.
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Any> {
        log.error("handleResponseStatusException ", ex)
        return buildErrorResponseEntity(
            ex.localizedMessage,
            ex.message,
            ex.status
        )
    }

    //Handle DataIntegrityViolationException, inspects the cause for different DB causes.
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<Any> {
        log.error("handleDataIntegrityViolation ", ex)
        return if (ex.cause is ConstraintViolationException) {
            buildErrorResponseEntity(
                DATABASE_ERROR, ex.localizedMessage,
                HttpStatus.BAD_REQUEST
            )
        }
        else buildErrorResponseEntity(
             INVALID_DATA, ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    //Handle Exceptions
    @ExceptionHandler(Exception::class)
    fun handleAllException(ex: Exception, request: WebRequest?): ResponseEntity<Any> {
        log.error("handleAllException ", ex)
        return buildErrorResponseEntity(
            ex.localizedMessage,
            listOf<String>(),
            HttpStatus.BAD_REQUEST
        )
    }

    //Handle GeneralExceptiom
    @ExceptionHandler(Throwable::class)
    fun handleAllException(ex: Throwable, request: WebRequest?): ResponseEntity<Any> {
        log.error("handleAllThrowableException ", ex)
        return buildErrorResponseEntity(

            ERROR_PLEASE_TRY_AGAIN,
            ex.localizedMessage, HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(HttpServerErrorException::class)
    fun handleHttpServerErrorException(ex: HttpServerErrorException): ResponseEntity<Any> {
        log.error("handleHttpServerErrorException ", ex)
        return buildErrorResponseEntity(

            CONNECTION_ERROR,
            ex.localizedMessage, HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<Any> {
        log.error("handleBadCredentialsException ", ex)
        return buildErrorResponseEntity(

            BAD_CREDENTIALS,
            ex.localizedMessage, HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException): ResponseEntity<Any> {
        log.error("handleUsernameNotFoundException ", ex)
        return buildErrorResponseEntity(
            USERNAME_NOT_FOUND,
            ex.localizedMessage, HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleAInsufficientAuthenticationException(ex: InsufficientAuthenticationException): ResponseEntity<Any> {
        log.error("handleAInsufficientAuthenticationException ", ex)
        return buildErrorResponseEntity(
            INSUFFICIENT_AUTHENTICATION,
            ex.localizedMessage, HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointerException(ex: NullPointerException): ResponseEntity<Any> {
        log.error("handleNullPointerException ", ex)
        return buildErrorResponseEntity(
            NULL_ERROR,
            ex.localizedMessage, HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException): ResponseEntity<Any> {
        log.error("handleNumberFormatException ", ex)
        return buildErrorResponseEntity(
            INVALID_NUMBER_FORMAT,
            ex.localizedMessage, HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(GenericException::class)
    fun handleGenericException(ex: GenericException): ResponseEntity<Any> {
        log.error("handleGenericException ", ex)
        return buildErrorResponseEntity(
            ex.localizedMessage,
            listOf<String>(),
            HttpStatus.BAD_REQUEST
        )
    }

     @ExceptionHandler(TokenRefreshExpiredException::class)
    fun handleTokenRefreshExpiredException(ex: TokenRefreshExpiredException): ResponseEntity<Any> {
        log.error("handleTokenRefreshExpiredException ", ex)
        return buildErrorResponseEntity(
            ex.localizedMessage,
            listOf<String>(),
            HttpStatus.BAD_REQUEST
        )
    }

     @ExceptionHandler(UserNameExistsException::class)
    fun handleUserNameExistsException(ex: UserNameExistsException): ResponseEntity<Any> {
        log.error("handleUserNameExistsException ", ex)
        return buildErrorResponseEntity(
            ex.localizedMessage,
            listOf<String>(),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(NonUniqueResultException::class)
    fun handleNonUniqueResultException(ex: NonUniqueResultException): ResponseEntity<Any> {
        log.error("handleNonUniqueResultException ", ex)
        return buildErrorResponseEntity(
            ENTITY_EXISTS,
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(SocketTimeoutException::class)
    fun handleSocketTimeoutException(ex: SocketTimeoutException): ResponseEntity<Any> {
        log.error("handleNonUniqueResultException ", ex)
        return buildErrorResponseEntity(
            API_TIMEOUT,
            ex.localizedMessage,
            HttpStatus.REQUEST_TIMEOUT
        )
    }

    @ExceptionHandler(InvalidUserNameException::class)
    fun handleInvalidUserNameException(ex: InvalidUserNameException): ResponseEntity<Any> {
        log.error("handleInvalidUserNameException ", ex)
        return buildErrorResponseEntity(
            ex.message,
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(InvalidRoleException::class)
    fun handleInvalidRoleException(ex: InvalidRoleException): ResponseEntity<Any> {
        log.error("handleInvalidRoleException ", ex)
        return buildErrorResponseEntity(
            ex.message,
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatchException(ex: PasswordMismatchException): ResponseEntity<Any> {
        log.error("handleInvalidUserNameException ", ex)
        return buildErrorResponseEntity(
            ex.message,
            ex.localizedMessage,
            HttpStatus.BAD_REQUEST
        )
    }

    private fun buildErrorResponseEntity(
       errorMessage: String, data: Any,
        status: HttpStatus
    ): ResponseEntity<Any> {
        val response = ApiResponse(
            error = true,
            message = errorMessage,
            data = data,
            status = status
        )
            
        return ResponseEntity<Any>(response, response.status)
    }
}