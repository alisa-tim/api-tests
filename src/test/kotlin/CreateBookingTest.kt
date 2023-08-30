import client.Booking
import client.DatesInterval
import client.api
import decoders.ApiError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CreateBookingTest {

    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5)),
        additionalNeeds = "late checkout"
    )

    @Test
    fun `create booking and check that response has correct data`() {
        val expected = booking
        val actual = api.createBooking(expected).booking
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking and check that you can get it by id`() {
        val expected = booking
        val id = api.createBooking(expected).id
        val actual = api.getBooking(id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking and check that it appears in the bookings list`() {
        val id = api.createBooking(booking).id
        val ids = api.getBookings().map { it.id }
        assertThat(ids).contains(id)
    }

    @Test
    fun `create booking without additional needs`() {
        val expected = booking.copy(additionalNeeds = null)
        val id = api.createBooking(expected).id
        val actual = api.getBooking(id)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking without firstname`() {
        val expected = booking.copy(firstName = null)
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without lastname`() {
        val expected = booking.copy(lastName = null)
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without total price`() {
        val expected = booking.copy(totalPrice = null)
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without deposit paid`() {
        val expected = booking.copy(depositPaid = null)
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without dates`() {
        val expected = booking.copy(bookingDates = null)
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without check in date`() {
        val expected = booking.copy(bookingDates = DatesInterval(checkout = LocalDate.now()))
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }

    @Test
    fun `create booking without check out date`() {
        val expected = booking.copy(bookingDates = DatesInterval(checkin = LocalDate.now()))
        val id = api.createBooking(expected).id
        val error = catchThrowable { api.getBooking(id) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }
}