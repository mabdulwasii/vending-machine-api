package com.vending.api.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

class TestUtils {
    companion object{
        fun asJsonString(any: Any): String {
            try {
                return ObjectMapper().writeValueAsString(any)
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
            }
            return ""
        }

    }

}
