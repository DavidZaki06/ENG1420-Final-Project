package Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class WaitlistManager {

    public boolean addToWaitlist(Booking booking, Event event) {

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
        return new ArrayList<>(event.getWaitlist());
    }

    public boolean removeFromWaitlist(Event event, String bookingId) {

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

        for (Booking b : event.getWaitlist()) {
            b.setStatus(BookingStatus.CANCELLED);
        }

        event.getWaitlist().clear();
    }

    public int getWaitlistSize(Event event) {
        return event.getWaitlist().size();
    }

    public int getPosition(Event event, String userId) {

        int pos = 1;

        for (Booking b : event.getWaitlist()) {
            if (b.getUserId().getUserId().equals(userId) &&
                    b.getBookingStatus() == BookingStatus.WAITLISTED) {
                return pos;
            }
            pos++;
        }

        return -1;
    }

    public boolean containsUser(Event event, String userId) {

        for (Booking b : event.getWaitlist()) {
            if (b.getUserId().getUserId().equals(userId) &&
                    b.getBookingStatus() == BookingStatus.WAITLISTED) {
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