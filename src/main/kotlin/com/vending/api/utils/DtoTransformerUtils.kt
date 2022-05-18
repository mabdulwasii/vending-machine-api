package com.vending.api.utils

import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.ProductDTO
import com.vending.api.dto.UserDTO
import com.vending.api.entity.Product
import com.vending.api.entity.User

class DtoTransformerUtils {
    companion object {
        fun transformCreateUserRequestToUserEntity(request: CreateUserRequest) = User(
            username = request.username,
            password = request.password
        )

        fun transformUserEntityToUserDto(user: User) = UserDTO(
            id = user.id,
            username = user.username,
            deposit = user.deposit,
            roles = user.roles.map { role -> role.name.name }.toMutableSet()
        )

        fun transformProductDtoToProduct(request: ProductDTO, user: User) = Product(
            request.id,
            request.amountAvailable,
            request.cost,
            request.productName,
            user
        )

        fun transformProductEntityToDto(request: Product) = ProductDTO(
                request.id,
                request.amountAvailable,
                request.cost,
                request.productName,
                request.seller.username
            )

        fun transformProductListToProctDtos(products: List<Product>): List<ProductDTO> {
            val productDTOS = mutableListOf<ProductDTO>()
            products.map {
                productDTOS.add(transformProductEntityToDto(it))
            }
            return productDTOS
        }
    }
}
