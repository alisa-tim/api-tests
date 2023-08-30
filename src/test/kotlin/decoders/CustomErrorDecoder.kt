package decoders

import feign.Response
import feign.codec.ErrorDecoder
import kotlin.Exception

class CustomErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
            return ApiError(response.status(), response.reason())
    }
}

data class ApiError (
    val statusCode: Int,
    val reason: String
) : RuntimeException(reason)