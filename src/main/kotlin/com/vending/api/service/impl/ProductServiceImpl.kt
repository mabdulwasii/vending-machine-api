package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.BuyProductDTO
import com.vending.api.dto.BuyProductResponse
import com.vending.api.dto.ProductDTO
import com.vending.api.exception.GenericException
import com.vending.api.exception.InvalidUserNameException
import com.vending.api.repository.ProductRepository
import com.vending.api.service.ProductService
import com.vending.api.service.UserService
import com.vending.api.utils.ApiResponseUtils.Companion.buildFailedApiResponse
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessApiResponse
import com.vending.api.utils.ConstantUtils.COULD_NOT_RETRIEVE_LOGIN_USER
import com.vending.api.utils.ConstantUtils.COULD_NOT_RETRIEVE_PRODUCT
import com.vending.api.utils.ConstantUtils.INSUFFICIENT_DEPOSIT
import com.vending.api.utils.ConstantUtils.INVALID_PRODUCT_ID
import com.vending.api.utils.ConstantUtils.INVALID_USER_PLEASE_LOGIN
import com.vending.api.utils.ConstantUtils.PRODUCT_CREATED_SUCCESSFULLY
import com.vending.api.utils.ConstantUtils.PRODUCT_NOT_FOUND
import com.vending.api.utils.ConstantUtils.PRODUCT_RETRIEVED
import com.vending.api.utils.ConstantUtils.PRODUCT_UPDATED_SUCCESSFULLY
import com.vending.api.utils.ConstantUtils.SUCCESS
import com.vending.api.utils.DtoTransformerUtils
import com.vending.api.utils.LoginUserUtils
import com.vending.api.utils.LoginUserUtils.Companion.ensureSellerIdMatches
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class ProductServiceImpl(
    val productRepository: ProductRepository,
    val userService: UserService,
) : ProductService {
    override suspend fun createProduct(request: ProductDTO): ApiResponse {
        userService.getUserByUsername(request.sellerId)?.let {
            var product = DtoTransformerUtils.transformProductDtoToProduct(request, it)
            product = productRepository.save(product)
            val productDTO = DtoTransformerUtils.transformProductEntityToDto(product)
            return buildSuccessApiResponse(productDTO, PRODUCT_CREATED_SUCCESSFULLY, HttpStatus.CREATED)
        } ?: return buildFailedApiResponse(null, COULD_NOT_RETRIEVE_LOGIN_USER, HttpStatus.NOT_FOUND)
    }

    override suspend fun updateUser(product: ProductDTO): ApiResponse {
        val retrievedProduct = productRepository.findByIdOrNull(product.id)
        retrievedProduct?.let {
            ensureSellerIdMatches(product.sellerId, it.seller.username)
            it.productName = product.productName
            it.cost = product.cost
            it.amountAvailable = product.amountAvailable
            val updatedProduct = productRepository.save(it)
            val productDTO = DtoTransformerUtils.transformProductEntityToDto(updatedProduct)
            return buildSuccessApiResponse(productDTO, PRODUCT_UPDATED_SUCCESSFULLY)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, COULD_NOT_RETRIEVE_PRODUCT)
    }

    override suspend fun findProduct(id: Long): ApiResponse {
        val product = productRepository.findByIdOrNull(id)
        product?.let {
            val productDTO = DtoTransformerUtils.transformProductEntityToDto(it)
            return buildSuccessApiResponse(productDTO, PRODUCT_RETRIEVED)
        } ?: return buildFailedApiResponse(null, PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND)
    }

    override suspend fun findAllProducts(): ApiResponse {
        val products = withContext(Dispatchers.IO) {
            productRepository.findAll()
        }
        val productDtoList = DtoTransformerUtils.transformProductListToProctDtos(products)
        return buildSuccessApiResponse(productDtoList, SUCCESS)
    }

    override suspend fun deleteProduct(id: Long): ApiResponse {
        val username = LoginUserUtils.getAuthUserId()
        username.let { user ->
            val product = productRepository.findByIdOrNull(id)
            product?.let {
                ensureSellerIdMatches(user, it.seller.username)
                productRepository.deleteById(id)
            }
        }
        return buildSuccessApiResponse(null, "", HttpStatus.NO_CONTENT)
    }

    private fun generateChange(deposit: Int): Array<Int> {
        val change = mutableListOf<Int>()
        val coins = listOf(100, 50, 20, 10, 5)
        var value: Int = deposit
        coins.forEach {
            if (it <= value) { //if coin is less than current change
                val count: Int = value / it //get how many denomination present
                for (i in count downTo 1) { //iterated down to 1 from the number of denomination
                    change.add(it) // Iterate Add denomination to the list
                }
                value -= it * count  // update value: minus amount in the array from value
            }
        }
        return change.toTypedArray()
    }

    override suspend fun buyProduct(buyProductDTO: BuyProductDTO): ApiResponse {
        val currentUsername = LoginUserUtils.getAuthUserId()
        userService.getUserByUsername(currentUsername)?.let { user ->
            val product = productRepository.findByIdOrNull(buyProductDTO.productId)
            product?.let {
                val amountOfProducts = buyProductDTO.amountOfProducts
                val totalCost = it.cost * amountOfProducts
                val amountAvailable = it.amountAvailable
                if (amountAvailable <= amountOfProducts) {
                    return buildFailedApiResponse(null, "Only ($amountAvailable) products available")
                }

                if (user.deposit <= totalCost) {
                    return buildFailedApiResponse(null, INSUFFICIENT_DEPOSIT)
                }
                user.deposit -= totalCost
                it.amountAvailable -= amountOfProducts
                val updatedUser = userService.save(user)
                val updatedProduct = productRepository.save(it)
                val productDTO = DtoTransformerUtils.transformProductEntityToDto(updatedProduct)
                val buyProductResponse =
                    BuyProductResponse(totalCost, productDTO, generateChange(updatedUser.deposit))
                return buildSuccessApiResponse(buyProductResponse, SUCCESS)

            } ?: throw GenericException(INVALID_PRODUCT_ID)
        } ?: throw InvalidUserNameException(INVALID_USER_PLEASE_LOGIN)
    }
}