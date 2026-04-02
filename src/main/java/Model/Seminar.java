package Model;

public class Seminar extends Event {

    private String speakerName; // specific to Seminar

    public Seminar(String eventId, String title, String dateTime,
                   String location, int capacity, String status,
                   String speakerName) {

        super(eventId, title, dateTime, location, capacity, status);
        this.speakerName = speakerName;
    }

    // Getter and Setter
    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    @Override
    public String getEventType() {
        return "Seminar";
    }
}
