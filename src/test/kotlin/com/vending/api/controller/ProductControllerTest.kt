package com.vending.api.controller

import com.vending.api.VendingApplication
import com.vending.api.controller.UserControllerTest.Companion.USERNAME
import com.vending.api.dto.ApiResponse
import com.vending.api.dto.BuyProductDTO
import com.vending.api.dto.BuyProductResponse
import com.vending.api.dto.ProductDTO
import com.vending.api.entity.Product
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
import com.vending.api.utils.ConstantUtils
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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

private const val AMOUNT_OF_PRODUCT = 3
private const val PRODUCT_ID = 1L
private const val AMOUNT_AVAILABLE = 5
private const val COST = 20
private const val PRODUCT_NAME = "Book"
private const val SELLER_ID = "Seller1234"

@SpringBootTest(classes = [VendingApplication::class])
@AutoConfigureMockMvc
class ProductControllerTest {

    final val userRepository: UserRepository = mockk()
    final val roleRepository: RoleRepository = mockk()
    final val refreshTokenRepository: RefreshTokenRepository = mockk()
    final val passwordEncoder: PasswordEncoder = mockk()
    val userServices: UserService = mockk()

    final val userService = UserServiceImpl(userRepository, roleRepository, refreshTokenRepository, passwordEncoder)

    final val productRepository : ProductRepository = mockk()

    val productService = ProductServiceImpl(productRepository, userService)

    final val roles = mutableSetOf<Role>()
    final val user = User(UserControllerTest.USER_ID, USERNAME, UserControllerTest.PASSWORD, UserControllerTest.DEFAULT_DEPOSIT, roles)
    val product = Product(PRODUCT_ID, AMOUNT_AVAILABLE, COST, PRODUCT_NAME, user)
    val productDTO = ProductDTO(PRODUCT_ID, AMOUNT_AVAILABLE, COST, PRODUCT_NAME, SELLER_ID)

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `should fail to buy product if user not authenticated`(): Unit = runBlocking() {
        mockMvc.post("/api/buy") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(BuyProductDTO(PRODUCT_ID, AMOUNT_OF_PRODUCT))
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser(roles = ["SELLER"])
    fun `should fail to buy product if user is not BUYER`(): Unit = runBlocking() {
        mockMvc.post("/api/buy") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(BuyProductDTO(PRODUCT_ID, AMOUNT_OF_PRODUCT))
            accept = MediaType.APPLICATION_JSON
        }.andReturn()
            .resolvedException?.let {
                Assertions.assertTrue(it is InvalidUserNameException)
                Assertions.assertEquals(ConstantUtils.INVALID_USER_PLEASE_LOGIN, it.message)
            }
    }

     @Test
    @WithMockUser(roles = ["BUYER"])
    fun `should buy product if user is a BUYER`(): Unit = runBlocking() {
         val buyProductDTO = BuyProductDTO(PRODUCT_ID, AMOUNT_OF_PRODUCT)
         every { userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(any()) } returns user
         mockkObject(LoginUserUtils)
         every { LoginUserUtils.getAuthUserId() } returns  USERNAME
         every { runBlocking { userServices.getUserByUsername(USERNAME) } } returns null
         mockkObject(ApiResponseUtils)
         val buyProductResponse = BuyProductResponse(60, productDTO, arrayOf(50, 20, 5))
         val response = ApiResponse(false, SUCCESS, buyProductResponse, HttpStatus.OK )
         every{ ApiResponseUtils.buildSuccessApiResponse(buyProductResponse, ConstantUtils.SUCCESS) } returns response
         every { productRepository.findByIdOrNull(PRODUCT_ID) } returns product
         every { userRepository.save(any()) } returns user
         every { runBlocking {  productService.buyProduct(buyProductDTO) } } returns response


        mockMvc.post("/api/buy") {
            contentType = MediaType.APPLICATION_JSON
            content = asJsonString(buyProductDTO)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

}