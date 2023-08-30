package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import decoders.CustomErrorDecoder
import feign.Feign
import feign.Headers
import feign.Param
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import interceptors.AllureLoggingInterceptor
import java.time.LocalDate

interface ApiClient {
    @RequestLine("GET /booking?firstname={firstname}&lastname={lastname}&checkin={checkin}&checkout={checkout}")
    fun getBookings(
        @Param("firstname") firstName: String? = null,
        @Param("lastname") lastName: String? = null,
        @Param("checkin") checkIn: LocalDate? = null,
        @Param("checkout") checkOut: LocalDate? = null
    ): List<BookingId>

    @RequestLine("GET /booking/{id}")
    fun getBooking(@Param("id") id: Int) : Booking

    @RequestLine("POST /booking")
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun createBooking(booking: Booking) : CreatedBooking

    @RequestLine("PUT /booking/{id}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Cookie: token={token}",
        "Authorization: Basic {token}"
    )
    fun updateBooking(@Param("token") token: String?, @Param("id") id: Int?, booking: Booking) : Booking

    @RequestLine("DELETE /booking/{id}")
    @Headers(
        "Content-Type: application/json",
        "Cookie: token={token}",
        "Authorization: Basic {token}"
    )
    fun deleteBooking(@Param("token") token: String?, @Param("id") id: Int)

    @RequestLine("POST /auth")
    @Headers("Content-Type: application/json")
    fun getToken(user: User): Token
}

val mapper: ObjectMapper = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(JavaTimeModule())
    .registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )


val httpClient = okhttp3.OkHttpClient.Builder()
    .addInterceptor(AllureLoggingInterceptor())
    .build()

val api: ApiClient = Feign.builder()
    .client(OkHttpClient(httpClient))
    .encoder(JacksonEncoder(mapper))
    .decoder(JacksonDecoder(mapper))
    .errorDecoder(CustomErrorDecoder())
    .target(ApiClient::class.java, "https://restful-booker.herokuapp.com")
