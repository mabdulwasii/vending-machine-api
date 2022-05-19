package com.vending.api.controller

import com.vending.api.VendingApplication
import com.vending.api.dto.ApiResponse
import com.vending.api.dto.Deposit
import com.vending.api.entity.Role
import com.vending.api.entity.User
import com.vending.api.exception.InvalidUserNameException
import com.vending.api.repository.ProductRepository
import com.vending.api.repository.RefreshTokenRepository
import com.vending.api.repository.RoleRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.UserService
import com.vending.api.service.impl.ProductServiceImpl
import com.vending.api.service.impl.UserServiceImpl
import com.vending.api.utils.ApiResponseUtils
import com.vending.api.utils.ConstantUtils.INVALID_USER_PLEASE_LOGIN
import com.vending.api.utils.ConstantUtils.SUCCESS
import com.vending.api.utils.LoginUserUtils
import com.vending.api.utils.TestUtils.Companion.asJsonString
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(classes = [VendingApplication::class])
@AutoConfigureMockMvc
internal class UserControllerTest {

    companion object {
        const val USER_ID = 1L
        const val USERNAME = "wenk"
        const val PASSWORD = "wenk123444##"
        const val VALID_AMOUNT = 20
        const val DEFAULT_DEPOSIT = 0
    }

    final val userRepository: UserRepository = mockk()
    final val productRepository: ProductRepository = mockk()
    final val roleRepository: RoleRepository = mockk()
    final val refreshTokenRepository: RefreshTokenRepository= mockk()
    final val passwordEncoder: PasswordEncoder = mockk()
    val userServices: UserService = mockk()

    final val userService = UserServiceImpl(userRepository, roleRepository, refreshTokenRepository, passwordEncoder)

    val productService = ProductServiceImpl(productRepository, userService)

    @Autowired
    private lateinit var mockMvc: MockMvc

    final val roles = mutableSetOf<Role>()
    val deposit = Deposit(amount = VALID_AMOUNT)
    val user = User(USER_ID, USERNAME, PASSWORD, DEFAULT_DEPOSIT, roles)

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `should fail to deposit coin if user not authenticated`(): Unit = runBlocking() {
        mockMvc.post("/api/deposit") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(Deposit(10))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }


    @Test
    @WithMockUser(roles = ["SELLER"])
    fun `should not return no content when deposit is called by user with role SELLER`(): Unit = runBlocking() {

        mockMvc.post("/api/deposit") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(Deposit(10))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    @WithMockUser(roles = ["BUYER"])
    fun `should fail to deposit if userRole is BUYER but username is null`(): Unit = runBlocking() {
        mockkObject(LoginUserUtils)
        every { LoginUserUtils.getAuthUserId() } throws InvalidUserNameException(INVALID_USER_PLEASE_LOGIN)

        mockMvc.post("/api/deposit") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(Deposit(10))
            accept = MediaType.APPLICATION_JSON
        }.andReturn()
            .resolvedException?.let {
                Assertions.assertTrue(it is InvalidUserNameException)
                Assertions.assertEquals(INVALID_USER_PLEASE_LOGIN, it.message)
            }
    }

    @Test
    @WithMockUser(roles = ["BUYER"], username = USERNAME)
    fun `should deposit if userRole is BUYER`(): Unit = runBlocking() {
        val apiResponse = ApiResponse(
            error = true,
            message = INVALID_USER_PLEASE_LOGIN,
            data = null,
            status = HttpStatus.BAD_REQUEST
        )
        every { userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(any()) } returns user
        mockkObject(LoginUserUtils)
        every { LoginUserUtils.getAuthUserId() } returns USERNAME
        every { runBlocking { userServices.getUserByUsername(USERNAME) } } returns null
        mockkObject(ApiResponseUtils)
        every { ApiResponseUtils.buildFailedApiResponse(null, INVALID_USER_PLEASE_LOGIN) } returns apiResponse
        every { userRepository.save(any()) } returns user

        mockMvc.post("/api/deposit") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(Deposit(10))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

     @Test
     @WithMockUser
    fun `should find all users`(): Unit = runBlocking() {
         val newUser = User(2L, "boyHod", "Pwaitrt$5@", 10, roles)
         val retrievedUsers = listOf<User>(user, newUser)
         every { userRepository.findAll() } returns retrievedUsers
         val apiResponse = ApiResponse(false, SUCCESS, retrievedUsers, HttpStatus.OK)
         every { runBlocking { userServices.findAllUsers() } } returns apiResponse

        mockMvc.get("/api/user") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }
}