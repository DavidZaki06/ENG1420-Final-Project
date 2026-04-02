package Model;

public class Workshop extends Event {

    private String topic; // specific to Workshop

    public Workshop(String eventId, String title, String dateTime,
                    String location, int capacity, String status,
                    String topic) {

        super(eventId, title, dateTime, location, capacity, status);
        this.topic = topic;
    }

    // Getter and Setter
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String getEventType() {
        return "Workshop";
    }
}