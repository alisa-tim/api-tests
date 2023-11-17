import client.*
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@Tag("update")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateBookingTest {

    private val token: String = RestAssured.given(spec)
        .body(User("admin", "password123"))
        .`when`()
        .post("/auth")
        .body()
        .path("token")
    private var id = 0
    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5)),
        additionalNeeds = "late checkout"
    )

    @BeforeAll
    fun createBooking() {
        id = RestAssured.given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .id
    }

    @Test
    fun `update booking and check that api returns 200 OK`() {
        val expected = booking.copy(firstName = "New name")
        RestAssured.given(spec)
            .header("Cookie", "token = $token")
            .body(expected)
            .`when`()
            .put("/booking/$id")
            .then()
            .assertThat()
            .statusCode(200)
    }

    @Test
    fun `update first name and check that api returns updated data`() {
        val expected = booking.copy(firstName = "New name")
        RestAssured.given(spec)
            .header("Cookie", "token = $token")
            .body(expected)
            .`when`()
            .put("/booking/$id")
        val actual = RestAssured.given(spec)
            .get("/booking/$id")
            .then()
            .extract()
            .`as`(Booking::class.java)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `update without first name and check that api returns 400`() {
        RestAssured.given(spec)
            .header("Cookie", "token = $token")
            .body(booking.copy(firstName = null))
            .`when`()
            .put("/booking/$id")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    fun `update with empty body and check that api returns 400`() {
        RestAssured.given(spec)
            .header("Cookie", "token = $token")
            .body(Booking())
            .`when`()
            .put("/booking/$id")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    fun `update without token and check that api returns 403`() {
        RestAssured.given(spec)
            .body(booking)
            .`when`()
            .put("/booking/$id")
            .then()
            .assertThat()
            .statusCode(403)
    }
}
