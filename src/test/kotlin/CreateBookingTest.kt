
import client.Booking
import client.CreatedBooking
import client.DatesInterval
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.hasItem
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

val spec = RequestSpecBuilder()
    .setBaseUri("https://restful-booker.herokuapp.com")
    .setContentType(ContentType.JSON)
    .addFilter(AllureRestAssured())
    .build()

@Tag("create")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBookingTest {

    val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5)),
        additionalNeeds = "late checkout"
    )

    @Test
    fun `create booking and check that response is 200 OK`() {
        given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .assertThat()
            .statusCode(200)
    }

    @Test
    fun `create booking and check that response has correct data`() {
        val actual = given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .booking
        assertThat(actual).isEqualTo(booking)
    }


    @Test
    fun `create booking and check that you can get it by id`() {
        val id = given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .id
        val actual = given(spec)
            .get("/booking/$id")
            .then()
            .extract()
            .`as`(Booking::class.java)
        assertThat(actual).isEqualTo(booking)
    }

    @Test
    fun `create booking and check that it appears in the bookings list`() {
        val id = given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .id
        given(spec)
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", hasItem(id))
    }

    @Test
    fun `create booking without additional needs`() {
        val expected = booking.copy(additionalNeeds = null)
        val id = given(spec)
            .body(expected)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .id
        val actual = given(spec)
            .get("/booking/$id")
            .then()
            .extract()
            .`as`(Booking::class.java)
        assertThat(actual).isEqualTo(expected)
    }

    fun invalidBookings(): Stream<Arguments> = Stream.of(
        Arguments.arguments("firstname", booking.copy(firstName = null)),
        Arguments.arguments("lastname", booking.copy(lastName = null)),
        Arguments.arguments("total price", booking.copy(totalPrice = null)),
        Arguments.arguments("deposit paid", booking.copy(depositPaid = null)),
        Arguments.arguments("dates", booking.copy(bookingDates = null)),
        Arguments.arguments(
            "check out date",
            booking.copy(bookingDates = DatesInterval(checkin = LocalDate.now()))
        ),
        Arguments.arguments(
            "check in date",
            booking.copy(bookingDates = DatesInterval(checkout = LocalDate.now()))
        )
    )

    @ParameterizedTest(name = "{0}" )
    @MethodSource("invalidBookings")
    @DisplayName("create booking without")
    fun `create booking without`(name: String, booking: Booking) {
        given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .assertThat()
            .statusCode(400)
    }
}