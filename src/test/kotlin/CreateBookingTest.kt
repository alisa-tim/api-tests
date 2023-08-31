import client.Booking
import client.DatesInterval
import client.api
import decoders.ApiError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate

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

    fun bookingsWithoutRequiredInfo(): List<Booking> = listOf(
        booking.copy(lastName = null),
        booking.copy(totalPrice = null),
        booking.copy(depositPaid = null),
        booking.copy(bookingDates = null),
        booking.copy(bookingDates = DatesInterval(checkout = LocalDate.now())),
        booking.copy(bookingDates = DatesInterval(checkin = LocalDate.now()))

    )

    @ParameterizedTest
    @MethodSource("bookingsWithoutRequiredInfo")
    fun `create booking without required info`(expected: Booking) {
        val error = catchThrowable { api.createBooking(expected) }
        assertThat(error).isEqualTo(ApiError(400, "Bad Request"))
    }
}