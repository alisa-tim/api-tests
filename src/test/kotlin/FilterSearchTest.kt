
import client.Booking
import client.CreatedBooking
import client.DatesInterval
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.*
import java.time.LocalDate

@Tag("search")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilterSearchTest {

    private var id = 0
    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5))
    )

    @BeforeAll
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
    fun `search booking by first name`() {
        given(spec)
            .param("firstname", booking.firstName)
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", CoreMatchers.hasItem(id))
    }

    @Test
    fun `search booking by last name`() {
        given(spec)
            .param("lastname", booking.lastName)
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", CoreMatchers.hasItem(id))
    }

    @Test
    fun `search booking by check in date`() {
        given(spec)
            .param("checkin", booking.bookingDates?.checkin.toString())
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", CoreMatchers.hasItem(id))
    }

    @Test
    fun `search booking by check out date`() {
        given(spec)
            .param("checkout", booking.bookingDates?.checkout.toString())
            .get("/booking")
            .then()
            .assertThat()
            .body("bookingid", CoreMatchers.hasItem(id))
    }
}
