package Model; // put this in the "model" package

public abstract class Event {

    // Common attributes for all events
    protected String eventId;    // Identifier
    protected String title;      // Event name
    protected String dateTime;   // Date and time of the event
    protected String location;   // Where it happens
    protected int capacity;      // Number of seats available, must be > 0
    protected String status;     // "Active" or "Cancelled"

    // Constructor
    public Event(String eventId, String title, String dateTime,
                 String location, int capacity, String status) {

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters
    public String getEventId(){
        return eventId; }
    public String getTitle(){
        return title; }
    public String getDateTime(){
        return dateTime; }
    public String getLocation(){
        return location; }
    public int getCapacity(){
        return capacity; }
    public String getStatus(){
        return status; }

    // Setters (for updating the event)
    public void setTitle(String title) { this.title = title; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
    }

    // Cancel the event
    public void cancelEvent() {
        this.status = "Cancelled";
    }

    // Method for event type (will be implemented by subclasses)
    public abstract String getEventType();
}
