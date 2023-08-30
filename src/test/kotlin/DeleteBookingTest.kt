import client.Booking
import client.DatesInterval
import client.User
import client.api
import decoders.ApiError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

class DeleteBookingTest {

    private var id = 0
    private val token = api.getToken(User("admin", "password123")).value
    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5))
    )

    @BeforeEach
    fun createBooking() {
        id = api.createBooking(booking).id
    }

    @Test
    fun `delete booking and check that it's not in the booking list`() {
        api.deleteBooking(token, id)
        val ids = api.getBookings().map { it.id }
        assertThat(ids).doesNotContain(id)
    }

    @Test
    fun `delete booking and check that you get 404 when trying to get it booking id`() {
        api.deleteBooking(token, id)
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(404, "Not Found"))
    }

    @Test
    fun `delete without token and check that api returns 403`() {
        val error = catchThrowable {
            api.deleteBooking(
                id = id,
                token = null
            )
        }
        assertThat(error).isEqualTo(ApiError(403, "Forbidden"))
    }
}