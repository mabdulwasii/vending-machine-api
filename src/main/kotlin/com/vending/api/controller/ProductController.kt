package com.vending.api.controller

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.BuyProductDTO
import com.vending.api.dto.ProductDTO
import com.vending.api.exception.GenericException
import com.vending.api.service.ProductService
import com.vending.api.utils.ConstantUtils.ID_NOT_NULL
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api",  consumes=["application/json"], produces = ["application/json"])
class ProductController(
    private val productService: ProductService
    ) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PreAuthorize("#request.sellerId == authentication.principal.username")
    @Secured("ROLE_SELLER")
    @PostMapping("/product")
    suspend fun createProduct(@RequestBody @Valid @P("request") request: ProductDTO): ResponseEntity<ApiResponse> {
        log.debug("REST request to create product: {}", request)
        val response = productService.createProduct(request)
        return ResponseEntity(response, response.status)
    }

    @PreAuthorize("#product.sellerId == authentication.principal.username")
    @Secured("ROLE_SELLER")
    @PutMapping("/product")
    suspend fun updateProduct(
        @RequestBody @Valid @P("product") product: ProductDTO
    ): ResponseEntity<ApiResponse> {
        log.debug("REST request to update a product : {}", product)
        try{
            product.id!!
        }catch (exception: Exception){
            throw GenericException(ID_NOT_NULL)
        }
        val response = productService.updateUser(product)
        return ResponseEntity(response, response.status)
    }

    @GetMapping("/product/{id}")
    suspend fun findUser(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        log.debug("REST request to find a User with id: {}", id)
        val response = productService.findProduct(id)
        return ResponseEntity(response, response.status)
    }

    @GetMapping("/product")
    suspend fun findAllProducts(): ResponseEntity<ApiResponse> {
        log.debug("REST request to find all Users: " )
        val response = productService.findAllProducts()
        return ResponseEntity(response, response.status)
    }

    @Secured("ROLE_SELLER")
    @DeleteMapping("/product/{id}/{sellerId}")
    suspend fun deleteProduct(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse> {
        log.debug("REST request to delete a User with id: {}", id)
        val response = productService.deleteProduct(id)
        return ResponseEntity(response, response.status)
    }

    @Secured("ROLE_BUYER")
    @PostMapping("/buy")
    suspend fun buyProduct(@RequestBody @Valid buyProductDTO: BuyProductDTO): ResponseEntity<ApiResponse> {
        log.debug("REST request to buy product: {}", buyProductDTO)
        val response = productService.buyProduct(buyProductDTO)
        return ResponseEntity(response, response.status)
    }
}