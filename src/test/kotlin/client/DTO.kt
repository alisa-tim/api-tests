package client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class BookingId(
    @JsonProperty("bookingid")
    val id: Int
)

data class User(
    val username: String,
    val password: String
)

data class Token(
    @JsonProperty("token")
    val value: String
)

@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
data class Booking(
    @JsonProperty("firstname")
    var firstName: String? = null,
    @JsonProperty("lastname")
    var lastName: String? = null,
    @JsonProperty("totalprice")
    var totalPrice: Int? = null,
    @JsonProperty("depositpaid")
    var depositPaid: Boolean? = null,
    @JsonProperty("bookingdates")
    var bookingDates: DatesInterval? = null,
    @JsonProperty("additionalneeds")
    var additionalNeeds: String? = null
)

data class DatesInterval(
    val checkin: LocalDate? = null,
    val checkout: LocalDate? = null
)

data class CreatedBooking(
    @JsonProperty("bookingid")
    val id: Int,
    val booking: Booking
)