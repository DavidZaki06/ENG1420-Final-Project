package Model;
import Model.Event;
import java.time.LocalDateTime;

public class Booking {
    String bookingId;
    User userId;
    Event event;
    LocalDateTime createdAt;
    BookingStatus status;

    public Booking() {
    }
    public Booking(String bookingId, User userId, Event event, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.event = event;
        this.createdAt = createdAt;
    }
    public String getBookingId(){
        return bookingId;
    }
    public User getUserId(){
        return userId;
    }
    public Event getEvent(){
        return event;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public BookingStatus getBookingStatus(){
        return status;
    }
    public void setStatus(BookingStatus status){
        this.status = status;
    }
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }
    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }
}
