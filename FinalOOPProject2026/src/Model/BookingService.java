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
String type = user.getUserType()
if(type == "Student" && user.getBookingLimit() >= 3) {
    throw new IllegalArgumentException("Booking limit reached.");
}else if(type == "Staff" && user.getBookingLimit() >= 5) {
    throw new IllegalArgumentException("Booking limit reached.");
}else if(type == "Guest" && user.getBookingLimit() >= 1) {
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
