import java.io.*;
import java.util.*;

public class FileLoader {

    // USERS
    public static ArrayList<User> loadUsers(String filePath) {
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                String userId = p[0];
                String name = p[1];
                String email = p[2];
                String type = p[3];

                User user;

                if (type.equalsIgnoreCase("Student")) {
                    user = new Student(userId, name, email);
                } else if (type.equalsIgnoreCase("Staff")) {
                    user = new Staff(userId, name, email);
                } else {
                    user = new Guest(userId, name, email);
                }

                users.add(user);
            }

        } catch (IOException e) {
            System.out.println("Error loading users file.");
        }

        return users;
    }

    // EVENTS
    public static ArrayList<Event> loadEvents(String filePath) {
        ArrayList<Event> events = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                String eventId = p[0];
                String title = p[1];
                String dateTime = p[2];
                String location = p[3];
                int capacity = Integer.parseInt(p[4]);
                String status = p[5];
                String type = p[6];

                Event event;

                if (type.equalsIgnoreCase("Workshop")) {
                    String topic = p[7];
                    event = new Workshop(eventId, title, dateTime, location, capacity, status, topic);

                } else if (type.equalsIgnoreCase("Seminar")) {
                    String speaker = p[8];
                    event = new Seminar(eventId, title, dateTime, location, capacity, status, speaker);

                } else { // Concert
                    String ageRestriction = p[9];
                    event = new Concert(eventId, title, dateTime, location, capacity, status, ageRestriction);
                }

                events.add(event);
            }

        } catch (IOException e) {
            System.out.println("Error loading events file.");
        }

        return events;
    }

    // BOOKINGS
    public static ArrayList<Booking> loadBookings(String filePath) {
        ArrayList<Booking> bookings = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                String bookingId = p[0];
                String userId = p[1];
                String eventId = p[2];
                String createdAt = p[3];
                String status = p[4];

                Booking booking = new Booking(bookingId, userId, eventId, createdAt, status);
                bookings.add(booking);
            }

        } catch (IOException e) {
            System.out.println("Error loading bookings file.");
        }

        return bookings;
    }
}
