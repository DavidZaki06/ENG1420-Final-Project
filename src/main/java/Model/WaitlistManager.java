package Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

/*
 WaitlistManager handles all waitlist-related logic for events.

 Responsibilities:
 - add users to waitlist when event is full
 - prevent duplicate waitlist entries
 - remove users from waitlist
 - promote users when space becomes available
 - track waitlist size and position
 - keep waitlist ordered by booking time (FIFO fairness)
*/

public class WaitlistManager {

    /*
     Adds a booking to the waitlist if:
     - booking exists
     - event exists
     - event is not cancelled
     - user is not already waitlisted

     Updates booking status → WAITLISTED
     Maintains sorted waitlist order
    */

    public boolean addToWaitlist(Booking booking, Event event) {
        if (booking == null || event == null) {
            return false;
        }

        if (event.getStatus().equalsIgnoreCase("Cancelled")) {
            return false;
        }

        if (containsUser(event, booking.getUserId().getUserId())) {
            return false;
        }

        booking.setStatus(BookingStatus.WAITLISTED);
        event.addToWaitlist(booking);
        sortWaitlist(event); // ensure earliest booking request stays first
        return true;
    }

    /*
     Returns a copy of the waitlist for UI display.

     Returning a copy protects the original queue
     from accidental modification by the interface layer.
    */
    public List<Booking> viewWaitlist(Event event) {
        if (event == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(event.getWaitlist());
    }

    /*
     Removes a booking from waitlist using booking ID.

     Also updates booking status → CANCELLED
     Ensures removal only happens if booking exists.
    */

    public boolean removeFromWaitlist(Event event, String bookingId) {
        if (event == null || bookingId == null) {
            return false;
        }

        Booking toRemove = null;

        for (Booking b : event.getWaitlist()) { // search waitlist for matching booking
            if (b.getBookingId().equals(bookingId)) {
                toRemove = b;
                break;
            }
        }

        if (toRemove == null) {
            return false;
        }

        event.getWaitlist().remove(toRemove);
        toRemove.setStatus(BookingStatus.CANCELLED);
        return true;
    }

     /*
     Automatically promotes next waitlisted booking
     when space becomes available in confirmed list.

     Promotion rules:
     - event must exist
     - event must be active
     - confirmed bookings must be below capacity
     - waitlist must not be empty
    */

    public Booking promoteNext(Event event) {
        if (event == null) {
            return null;
        }

        if (event.getStatus().equalsIgnoreCase("Cancelled")) {
            return null;
        }

        if (event.getConfirmedBookings().size() >= event.getCapacity()) {
            return null;
        }

        if (event.getWaitlist().isEmpty()) {
            return null;
        }

        Booking promoted = event.getWaitlist().poll(); // promote first user in queue (FIFO)
        promoted.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(promoted);
        return promoted;
    } /*
     Clears entire waitlist when event becomes cancelled.

     Ensures all waitlisted bookings are marked CANCELLED
     before removing them.
    */

    public void clearWaitlist(Event event) {
        if (event == null) {
            return;
        }

        for (Booking b : event.getWaitlist()) {
            b.setStatus(BookingStatus.CANCELLED);
        }

        event.getWaitlist().clear();
    }  /*
     Returns number of users currently on waitlist.
     Used for UI display and status summaries.
    */

    public int getWaitlistSize(Event event) {
        if (event == null) {
            return 0;
        }
        return event.getWaitlist().size();
    } /*
     Returns position of a user in waitlist queue.

     Position starts at 1 (not index 0)
     Returns -1 if user is not on waitlist.
    */

    public int getPosition(Event event, String userId) {
        if (event == null || userId == null) {
            return -1;
        }

        int position = 1;

        for (Booking b : event.getWaitlist()) {
            if (b.getUserId().getUserId().equals(userId)
                    && b.getBookingStatus() == BookingStatus.WAITLISTED) {
                return position;
            }
            position++;
        }

        return -1;
    }  /*
     Checks whether user already exists in waitlist.

     Prevents duplicate waitlist entries.
    */

    public boolean containsUser(Event event, String userId) {
        if (event == null || userId == null) {
            return false;
        }

        for (Booking b : event.getWaitlist()) {
            if (b.getUserId().getUserId().equals(userId)
                    && b.getBookingStatus() == BookingStatus.WAITLISTED) {
                return true;
            }
        }

        return false;
    }
    /*
     Sorts waitlist based on booking creation time.

     Ensures fairness:
     earliest booking request = highest priority
    */
    private void sortWaitlist(Event event) {
        List<Booking> sorted = new ArrayList<>(event.getWaitlist());
        sorted.sort(Comparator.comparing(Booking::getCreatedAt));

        Queue<Booking> waitlist = event.getWaitlist();
        waitlist.clear();
        waitlist.addAll(sorted);
    }
}