import client.Booking
import client.DatesInterval
import client.User
import client.api
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Tag("delete")
class DeleteBookingTest {

    private var id = 0
    private val token = api.getToken(User("admin", "password123")).execute().body()!!.value
    private val booking = Booking(
        firstName = "Test",
        lastName = "User",
        totalPrice = 99,
        depositPaid = true,
        bookingDates = DatesInterval(LocalDate.now(), LocalDate.now().plusDays(5))
    )

    @BeforeEach
    fun createBooking() {
        id = api.createBooking(booking).execute().body()!!.id
    }

    @Test
    fun `delete booking and check that api returns 201 created`() {
        val response = api.deleteBooking("token = $token", id).execute()
        assertThat(response.code()).isEqualTo(201)
        assertThat(response.message()).isEqualTo("Created")
    }

    @Test
    fun `delete booking and check that it's not in the booking list`() {
        api.deleteBooking("token = $token", id).execute()
        val ids = api.getBookings().execute().body()!!.map { it.id }
        assertThat(ids).doesNotContain(id)
    }

    @Test
    fun `delete booking and check that you get 404 when trying to get it booking id`() {
        api.deleteBooking("token = $token", id).execute()
        val response = api.getBooking(id).execute()
        assertThat(response.code()).isEqualTo(404)
        assertThat(response.message()).isEqualTo("Not Found")
    }

    @Test
    fun `delete without token and check that api returns 403`() {
        val response = api.deleteBooking(
                id = id,
                token = null
            ).execute()
        assertThat(response.code()).isEqualTo(403)
        assertThat(response.message()).isEqualTo("Forbidden")
    }
}
