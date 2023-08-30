package interceptors

import client.mapper
import io.qameta.allure.Allure
import io.qameta.allure.model.StepResult
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.UUID

class AllureLoggingInterceptor : Interceptor {

    val lifecycle = Allure.getLifecycle()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        lifecycle.startStep(UUID.randomUUID().toString(), StepResult().setName("${request.method} ${request.url}"))
        val requestLog = """
            Request: ${request.method} ${request.url}
            Headers: ${request.headers}
            Body: ${serializeRequestBodyToJson(request.body)}
        """.trimIndent()
        Allure.attachment("Request", requestLog)

        val response = chain.proceed(request)

        val responseLog = """
    |Response: ${response.code} ${response.message}
    |Headers: ${response.headers}
    |Body: ${serializeResponseBodyToJson(response.peekBody(Long.MAX_VALUE))}
""".trimMargin()

        Allure.attachment("Response", responseLog)
        lifecycle.stopStep()

        return response
    }

    private fun serializeRequestBodyToJson(requestBody: RequestBody?): String {
        if (requestBody == null) {
            return "No request body"
        }

        val buffer = okio.Buffer()
        requestBody.writeTo(buffer)

        return try {
            val source = buffer.readByteString().utf8()
            val jsonNode = mapper.readTree(source)
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
        } catch (e: Exception) {
            "Unable to parse request body as JSON: ${e.message}"
        }
    }

    private fun serializeResponseBodyToJson(responseBody: ResponseBody?): String {
        if (responseBody == null) {
            return "No response body"
        }
        return try {
            val jsonNode = mapper.readTree(responseBody.byteStream())
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
        } catch (e: Exception) {
            "Unable to parse response body as JSON: ${e.message}"
        }
    }
}