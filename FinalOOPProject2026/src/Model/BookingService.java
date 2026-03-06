package Model;
import java.util.*;
import Model.Event;
//import Model.User;

public class BookingService {

    public void createdBooking(Booking b, User user, Event event){
        // no duplicates
        if (alreadyBooked(user, event)){
            throw new IllegalArgumentException("User already booked.");
        }
        //booking limits by user-type
String type = user.getUserType();
    
// Count user's confirmed bookings
        int confirmedCount = 0;
        for (Booking booking : event.getConfirmedBookings()) {
            if (booking.getUserId().equals(user)) {
                confirmedCount++;
            }
        }
if(type.equals("Student") && confirmedCount >= 3) {
    throw new IllegalArgumentException("Booking limit reached.");
}else if(type.equals("Staff") && confirmedCount >= 5) {
    throw new IllegalArgumentException("Booking limit reached.");
}else if(type.equals("Guest") && confirmedCount >= 1) {
    throw new IllegalArgumentException("Booking limit reached.");
}
        // capacity-based confirmed/waitlist placement
        int capacity = event.getCapacity();
        if (event.getConfirmedBookings().size() < capacity){
            b.setStatus(BookingStatus.CONFIRMED);
        }else{
            b.setStatus(BookingStatus.WAITLISTED);
        }
    }
    public void cancelBooking(Booking b){
        b.cancel();
    }
    // display all bookings
    public void viewBookings(){

    }

    public boolean alreadyBooked(User user, Event event) {

        // To prevent duplicate bookings
        for (Booking b : event.getConfirmedBookings()) { // for each booking listed in confirmed bookings
            if (b.getUserId().equals(user) && b.getBookingStatus() != BookingStatus.CANCELLED) { //this will be subject to change
                return true;
            }
        }
        for (Booking b : event.getWaitlist()) { // for each booking listed in waitlisted bookings
            if (b.getUserId().equals(user) && b.getBookingStatus() != BookingStatus.CANCELLED) { //this will be subject to change
                return true;
            }
        }
        return false;
    }
}
