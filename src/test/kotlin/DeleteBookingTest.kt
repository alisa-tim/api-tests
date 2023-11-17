
import client.*
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Tag("delete")
class DeleteBookingTest {

    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5))
    )
    private val token: String = given(spec)
        .body(User("admin", "password123"))
        .`when`()
        .post("/auth")
        .body()
        .path("token")
    private var id = 0

    @BeforeEach
    fun createBooking() {
        id = given(spec)
            .body(booking)
            .`when`()
            .post("/booking")
            .then()
            .extract()
            .`as`(CreatedBooking::class.java)
            .id
    }

    @Test
    fun `delete booking and check that api returns 201 created`() {
        given(spec)
            .header("Cookie", "token = $token")
            .delete("/booking/$id")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    fun `delete booking and check that it's not in the booking list`() {
        given(spec)
            .header("Cookie", "token = $token")
            .delete("/booking/$id")
        given(spec)
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", CoreMatchers.not(hasItem(id)))
    }

    @Test
    fun `delete booking and check that you get 404 when trying to get it booking id`() {
        given(spec)
            .header("Cookie", "token = $token")
            .delete("/booking/$id")
        given(spec)
            .get("/booking/$id")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    fun `delete without token and check that api returns 403`() {
        given(spec)
            .delete("/booking/$id")
            .then()
            .assertThat()
            .statusCode(403)
    }
}
