import client.Booking
import client.DatesInterval
import client.User
import client.api
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateBookingTest {

    private val token = api.getToken(User("admin", "password123")).execute().body()!!.value
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
        id = api.createBooking(booking).execute().body()!!.id
    }

    @Test
    fun `update booking and check that api returns 200 OK`() {
        val expected = booking.copy(firstName = "New name")
        val response = api.updateBooking(
            id = id,
            token = "token = $token",
            booking = expected
        ).execute()
        assertThat(response.code()).isEqualTo(200)
        assertThat(response.message()).isEqualTo("OK")
    }

    @Test
    fun `update first name and check that api returns updated data`() {
        val expected = booking.copy(firstName = "New name")
        api.updateBooking(
            id = id,
            token = "token = $token",
            booking = expected
        )
        val actual = api.getBooking(id).execute().body()!!
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `update without first name and check that api returns 400`() {
        val response =
            api.updateBooking(
                id = id,
                token = "token = $token",
                booking = booking.copy(firstName = null)
            ).execute()
        assertThat(response.code()).isEqualTo(400)
        assertThat(response.message()).isEqualTo("Bad Request")
    }

    @Test
    fun `update with empty body and check that api returns 400`() {
        val response =
            api.updateBooking(
                id = id,
                token = "token = $token",
                booking = Booking()
            ).execute()
        assertThat(response.code()).isEqualTo(400)
        assertThat(response.message()).isEqualTo("Bad Request")
    }

    @Test
    fun `update without token and check that api returns 403`() {
        val response =
            api.updateBooking(
                id = id,
                token = null,
                booking = booking.copy(firstName = null)
            ).execute()
        assertThat(response.code()).isEqualTo(403)
        assertThat(response.message()).isEqualTo("Forbidden")
    }
}
