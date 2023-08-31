package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import interceptors.AllureLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.*
import java.time.LocalDate

interface ApiClient {
    @GET("booking")
    fun getBookings(
        @Query("firstname") firstName: String? = null,
        @Query("lastname") lastName: String? = null,
        @Query("checkin") checkIn: LocalDate? = null,
        @Query("checkout") checkOut: LocalDate? = null
    ): Call<List<BookingId>>

    @Headers("Accept: application/json")
    @GET("booking/{id}")
    fun getBooking(@Path("id") id: Int) : Call<Booking>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("booking")
    fun createBooking(@Body booking: Booking) : Call<CreatedBooking>

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @PUT("booking/{id}")
    fun updateBooking(@Header("Cookie") token: String?, @Path("id") id: Int?, @Body booking: Booking) : Call<Booking>

    @Headers("Content-Type: application/json")
    @DELETE("booking/{id}")
    fun deleteBooking(@Header("Cookie") token: String?, @Path("id") id: Int) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("auth")
    fun getToken(@Body user: User): Call<Token>
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

val api = Retrofit.Builder()
    .baseUrl("https://restful-booker.herokuapp.com/")
    .client(httpClient)
    .addConverterFactory(JacksonConverterFactory.create(mapper))
    .build()
    .create(ApiClient::class.java)