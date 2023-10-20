import client.Booking
import client.DatesInterval
import client.api
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDate

@Tag("create")
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
    fun `create booking and check that response is 200 OK`() {
        val response = api.createBooking(booking).execute()
        assertThat(response.code()).isEqualTo(200)
        assertThat(response.message()).isEqualTo("OK")
    }

    @Test
    fun `create booking and check that response has correct data`() {
        val expected = booking
        val actual = api.createBooking(expected).execute().body()!!.booking
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking and check that you can get it by id`() {
        val expected = booking
        val id = api.createBooking(expected).execute().body()!!.id
        val actual = api.getBooking(id).execute().body()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking and check that it appears in the bookings list`() {
        val id = api.createBooking(booking).execute().body()!!.id
        val ids = api.getBookings().execute().body()!!.map { it.id }
        assertThat(ids).contains(id)
    }

    @Test
    fun `create booking without additional needs`() {
        val expected = booking.copy(additionalNeeds = null)
        val id = api.createBooking(expected).execute().body()!!.id
        val actual = api.getBooking(id).execute().body()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `create booking without firstname`() {
        val expected = booking.copy(firstName = null)
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without lastname`() {
        val expected = booking.copy(lastName = null)
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without total price`() {
        val expected = booking.copy(totalPrice = null)
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without deposit paid`() {
        val expected = booking.copy(depositPaid = null)
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without dates`() {
        val expected = booking.copy(bookingDates = null)
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without check in date`() {
        val expected = booking.copy(bookingDates = DatesInterval(checkout = LocalDate.now()))
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }

    @Test
    fun `create booking without check out date`() {
        val expected = booking.copy(bookingDates = DatesInterval(checkin = LocalDate.now()))
        val response = api.createBooking(expected).execute()
        assertAll(
            { assertThat(response.code()).isEqualTo(400) },
            { assertThat(response.message()).isEqualTo("Bad Request") }
        )
    }
}