import client.Booking
import client.DatesInterval
import client.api
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
        id = api.createBooking(booking).execute().body()!!.id
    }

    @Test
    fun `search booking by first name`() {
        val ids = api.getBookings(firstName = booking.firstName).execute().body()!!.map { id }
        assertThat(ids).contains(id)
    }

    @Test
    fun `search booking by last name`() {
        val ids = api.getBookings(lastName = booking.lastName).execute().body()!!.map { id }
        assertThat(ids).contains(id)
    }

    @Test
    fun `search booking by check in date`() {
        val ids = api.getBookings(checkIn = booking.bookingDates?.checkin).execute().body()!!.map { it.id }
        assertThat(ids).containsOnlyOnce(id)
    }

    @Test
    fun `search booking by check out date`() {
        val ids = api.getBookings(checkOut = booking.bookingDates?.checkout).execute().body()!!.map { it.id }
        assertThat(ids).containsOnlyOnce(id)
    }
}
