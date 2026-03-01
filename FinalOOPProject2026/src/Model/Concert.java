package Model;

public class Concert extends Event {

    private String ageRestriction; // specific to Concert

    public Concert(String eventId, String title, String dateTime,
                   String location, int capacity, String status,
                   String ageRestriction) {

        super(eventId, title, dateTime, location, capacity, status);
        this.ageRestriction = ageRestriction;
    }

    // Getter and Setter
    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    @Override
    public String getEventType() {
        return "Concert";
    }
}