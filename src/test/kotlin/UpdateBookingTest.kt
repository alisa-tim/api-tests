import client.Booking
import client.DatesInterval
import client.User
import client.api
import decoders.ApiError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateBookingTest {

    private val token = api.getToken(User("admin", "password123")).value
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
        id = api.createBooking(booking).id
    }

    @Test
    fun `update first name and check that api returns updated data`() {
        val expected = booking.copy(firstName = "New name")
        api.updateBooking(
            id = id,
            token = token,
            booking = expected
        )
        val actual = api.getBooking(id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `update without first name and check that api returns 400`() {
        val error = catchThrowable {
            api.updateBooking(
                id = id,
                token = token,
                booking = booking.copy(firstName = null)
            )
        }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `update with empty body and check that api returns 400`() {
        val error = catchThrowable {
            api.updateBooking(
                id = id,
                token = token,
                booking = Booking()
            )
        }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `update without token and check that api returns 403`() {
        val error = catchThrowable {
            api.updateBooking(
                id = id,
                token = null,
                booking = booking.copy(firstName = null)
            )
        }
        assertThat(error).isEqualTo(ApiError(403, "Forbidden"))
    }
}