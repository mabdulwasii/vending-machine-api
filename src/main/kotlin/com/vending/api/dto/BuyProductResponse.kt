package com.vending.api.dto

data class BuyProductResponse(
    val totalCost: Int,
    val productDTO: ProductDTO,
    val change: Array<Int> = arrayOf<Int>()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuyProductResponse

        if (totalCost != other.totalCost) return false
        if (productDTO != other.productDTO) return false
        if (!change.contentEquals(other.change)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalCost
        result = 31 * result + productDTO.hashCode()
        result = 31 * result + change.contentHashCode()
        return result
    }
}
