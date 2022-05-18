package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.ProductDTO
import com.vending.api.repository.ProductRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.ProductService
import com.vending.api.utils.ApiResponseUtils.Companion.buildFailedApiResponse
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessApiResponse
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
    val userRepository: UserRepository,
) : ProductService {
    override suspend fun createProduct(request: ProductDTO): ApiResponse {
        val currentUser = withContext(Dispatchers.IO) {
            userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(request.sellerId)
        }
        currentUser?.let {
            var product = DtoTransformerUtils.transformProductDtoToProduct(request, it)
            product = productRepository.save(product)
            val productDTO = DtoTransformerUtils.transformProductEntityToDto(product)
            return buildSuccessApiResponse(productDTO, "Product created successfully", HttpStatus.CREATED)
        } ?: return buildFailedApiResponse(null, "Could not retrieve login user", HttpStatus.NOT_FOUND)
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
            return buildSuccessApiResponse(productDTO, "Product updated successfully")
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve product with the provided Id")
    }

    override suspend fun findProduct(id: Long): ApiResponse {
        val product = productRepository.findByIdOrNull(id)
        product?.let {
            val productDTO = DtoTransformerUtils.transformProductEntityToDto(it)
            return buildSuccessApiResponse(productDTO, "Product retrieved successfully")
        } ?: return buildFailedApiResponse(null, "Product not found", HttpStatus.NOT_FOUND)
    }

    override suspend fun findAllProducts(): ApiResponse {
        val products = withContext(Dispatchers.IO) {
            productRepository.findAll()
        }
        val productDtoList = DtoTransformerUtils.transformProductListToProctDtos(products)
        return buildSuccessApiResponse(productDtoList, "Success")
    }

    override suspend fun deleteProduct(id: Long): ApiResponse {
        val username = LoginUserUtils.getAuthUserId()
        username.let { user ->
            val product = productRepository.findByIdOrNull(id)
            product?.let {
                ensureSellerIdMatches(username, it.seller.username)
                productRepository.deleteById(id)
            }
        }
        return buildSuccessApiResponse(null, "", HttpStatus.NO_CONTENT)
    }
}