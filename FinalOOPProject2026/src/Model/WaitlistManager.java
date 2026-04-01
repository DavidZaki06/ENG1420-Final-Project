package Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class WaitlistManager {

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
        sortWaitlist(event);
        return true;
    }

    public List<Booking> viewWaitlist(Event event) {
        if (event == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(event.getWaitlist());
    }

    public boolean removeFromWaitlist(Event event, String bookingId) {
        if (event == null || bookingId == null) {
            return false;
        }

        Booking toRemove = null;

        for (Booking b : event.getWaitlist()) {
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

        Booking promoted = event.getWaitlist().poll();
        promoted.setStatus(BookingStatus.CONFIRMED);
        event.addConfirmedBooking(promoted);
        return promoted;
    }

    public void clearWaitlist(Event event) {
        if (event == null) {
            return;
        }

        for (Booking b : event.getWaitlist()) {
            b.setStatus(BookingStatus.CANCELLED);
        }

        event.getWaitlist().clear();
    }

    public int getWaitlistSize(Event event) {
        if (event == null) {
            return 0;
        }
        return event.getWaitlist().size();
    }

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
    }

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

    private void sortWaitlist(Event event) {
        List<Booking> sorted = new ArrayList<>(event.getWaitlist());
        sorted.sort(Comparator.comparing(Booking::getCreatedAt));

        Queue<Booking> waitlist = event.getWaitlist();
        waitlist.clear();
        waitlist.addAll(sorted);
    }
}