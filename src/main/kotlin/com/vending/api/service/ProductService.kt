package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.BuyProductDTO
import com.vending.api.dto.ProductDTO

interface ProductService {
    suspend fun createProduct(request: ProductDTO): ApiResponse
    suspend fun updateUser(product: ProductDTO): ApiResponse
    suspend fun findProduct(id: Long): ApiResponse
    suspend fun findAllProducts(): ApiResponse
    suspend fun deleteProduct(id: Long): ApiResponse
    suspend fun buyProduct(buyProductDTO: BuyProductDTO): ApiResponse

}