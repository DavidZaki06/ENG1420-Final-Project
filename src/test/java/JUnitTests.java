import Model.*;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class JUnitTests {

    private Event event;
    private User student;
    private User staff;

    @BeforeEach
    void setUp() {
        // Create a test event with capacity 1
        event = new Workshop("E001", "Test Workshop", "2024-01-01T10:00",
                "Room 101", 1, "Active", "Java Testing");

        // Create test users
        student = new Student("U001", "John Student", "john@uoguelph.ca");
        staff = new Staff("U002", "Sarah Staff", "sarah@uoguelph.ca");
    }

    // Test 1: Booking under capacity
    @Test
    void testBookingUnderCapacity() {
        Booking booking = new Booking("B001", student, event, LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(booking);

        assertEquals(1, event.getConfirmedBookings().size());
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
    }

    // Test 2: Booking when full → waitlist
    @Test
    void testBookingWhenFullGoesToWaitlist() {
        // Fill the event (capacity is 1)
        Booking booking1 = new Booking("B001", student, event, LocalDateTime.now());
        booking1.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(booking1);

        // Second booking should be waitlisted
        Booking booking2 = new Booking("B002", staff, event, LocalDateTime.now());
        booking2.setStatus(BookingStatus.WAITLISTED);
        event.addToWaitlist(booking2);

        assertEquals(1, event.getWaitlist().size());
        assertEquals(BookingStatus.WAITLISTED, booking2.getBookingStatus());
    }

    // Test 3: Cancel booking → waitlist promotion
    @Test
    void testCancelPromotesWaitlist() {
        // Fill capacity
        Booking booking1 = new Booking("B001", student, event, LocalDateTime.now());
        booking1.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(booking1);

        // Add to waitlist
        Booking booking2 = new Booking("B002", staff, event, LocalDateTime.now());
        booking2.setStatus(BookingStatus.WAITLISTED);
        event.addToWaitlist(booking2);

        // Cancel the confirmed booking
        event.cancelABooking(booking1);

        assertEquals(1, event.getConfirmedBookings().size());
        assertEquals(BookingStatus.CONFIRMED, booking2.getBookingStatus());
        assertEquals(0, event.getWaitlist().size());
    }

    // Test 4: Duplicate booking prevention
    @Test
    void testDuplicateBookingPrevention() {
        // First booking
        Booking booking1 = new Booking("B001", student, event, LocalDateTime.now());
        booking1.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(booking1);

        // Check if user is already booked
        boolean alreadyBooked = false;
        for (Booking b : event.getConfirmedBookings()) {
            if (b.getUserId().equals(student)) {
                alreadyBooked = true;
                break;
            }
        }

        assertTrue(alreadyBooked, "User should already have a booking");
    }
}