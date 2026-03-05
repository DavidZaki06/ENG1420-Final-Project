package UI;

import Model.*;
import java.util.ArrayList;
import java.util.Scanner;

public class EventUI {
    private Scanner scanner;
    private ArrayList<Event> events;  // Simple storage

    public EventUI(Scanner s) {
        this.scanner = s;
        this.events = new ArrayList<>();

        // Add sample events for testing
        events.add(new Workshop("W001", "Java Programming", "2024-06-15 14:00",
                "Room 101", 20, "Active", "Object-Oriented Design"));
        events.add(new Seminar("S001", "AI in Healthcare", "2024-06-16 10:00",
                "Auditorium", 50, "Active", "Dr. Sarah Chen"));
        events.add(new Concert("C001", "Summer Jazz", "2024-06-17 20:00",
                "Main Stage", 100, "Active", "All Ages"));
        events.add(new Workshop("W002", "Python Basics", "2024-06-18 09:00",
                "Lab 202", 15, "Active", "Data Structures"));
        events.add(new Concert("C002", "Rock Night", "2024-06-19 21:00",
                "Club House", 75, "Active", "18+"));
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("          EVENT MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("1. Create Event");
            System.out.println("2. Update Event");
            System.out.println("3. Cancel Event");
            System.out.println("4. View Event Roster");
            System.out.println("5. Search Events by Title");
            System.out.println("6. Filter Events by Type");
            System.out.println("7. List All Events");
            System.out.println("8. Back to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("Choice: ");

            int choice = getInt();

            switch (choice) {
                case 1: createEvent(); break;
                case 2: updateEvent(); break;
                case 3: cancelEvent(); break;
                case 4: viewEventRoster(); break;
                case 5: searchByTitle(); break;
                case 6: filterByType(); break;
                case 7: listAllEvents(); break;
                case 8: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    // Create Event

    private void createEvent() {
        System.out.println("\n--- CREATE EVENT ---");

        // Get event type
        System.out.println("Select event type:");
        System.out.println("1. Workshop");
        System.out.println("2. Seminar");
        System.out.println("3. Concert");
        System.out.print("Choice: ");
        int type = getInt();

        // Common fields for all events
        System.out.print("Enter Event ID: ");
        String eventId = scanner.nextLine();

        // Check for duplicate ID
        if (findEventById(eventId) != null) {
            System.out.println("Event ID already exists!");
            return;
        }

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Date/Time (YYYY-MM-DD HH:MM): ");
        String dateTime = scanner.nextLine();

        System.out.print("Enter Location: ");
        String location = scanner.nextLine();

        System.out.print("Enter Capacity (must be > 0): ");
        int capacity = getInt();

        if (capacity <= 0) {
            System.out.println("Capacity must be greater than 0");
            return;
        }

        String status = "Active";

        // Create specific event type with its extra field
        switch (type) {
            case 1: // Workshop
                System.out.print("Enter Topic: ");
                String topic = scanner.nextLine();
                events.add(new Workshop(eventId, title, dateTime, location,
                        capacity, status, topic));
                System.out.println("Workshop created successfully!");
                break;

            case 2: // Seminar
                System.out.print("Enter Speaker Name: ");
                String speaker = scanner.nextLine();
                events.add(new Seminar(eventId, title, dateTime, location,
                        capacity, status, speaker));
                System.out.println("Seminar created successfully!");
                break;

            case 3: // Concert
                System.out.print("Enter Age Restriction: ");
                String age = scanner.nextLine();
                events.add(new Concert(eventId, title, dateTime, location,
                        capacity, status, age));
                System.out.println("Concert created successfully!");
                break;

            default:
                System.out.println("Invalid event type");
        }
    }

    // Update Event

    private void updateEvent() {
        System.out.print("\nEnter Event ID to update: ");
        String eventId = scanner.nextLine();

        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        System.out.println("\n--- UPDATING EVENT ---");
        displayEventSummary(event);
        System.out.println("\n(Press Enter to keep current value)");

        // Update common fields
        System.out.print("New Title [" + event.getTitle() + "]: ");
        String input = scanner.nextLine();
        if (!input.isEmpty()) event.setTitle(input);

        System.out.print("New Date/Time [" + event.getDateTime() + "]: ");
        input = scanner.nextLine();
        if (!input.isEmpty()) event.setDateTime(input);

        System.out.print("New Location [" + event.getLocation() + "]: ");
        input = scanner.nextLine();
        if (!input.isEmpty()) event.setLocation(input);

        System.out.print("New Capacity [" + event.getCapacity() + "]: ");
        input = scanner.nextLine();
        if (!input.isEmpty()) {
            try {
                int newCap = Integer.parseInt(input);
                if (newCap > 0) {
                    event.setCapacity(newCap);
                } else {
                    System.out.println("Capacity must be > 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }

        // Update type-specific fields
        if (event instanceof Workshop) {
            Workshop w = (Workshop) event;
            System.out.print("New Topic [" + w.getTopic() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) w.setTopic(input);

        } else if (event instanceof Seminar) {
            Seminar s = (Seminar) event;
            System.out.print("New Speaker [" + s.getSpeakerName() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) s.setSpeakerName(input);

        } else if (event instanceof Concert) {
            Concert c = (Concert) event;
            System.out.print("New Age Restriction [" + c.getAgeRestriction() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) c.setAgeRestriction(input);
        }

        System.out.println("✅ Event updated successfully!");
    }

    // Cancel Event

    private void cancelEvent() {
        System.out.print("\nEnter Event ID to cancel: ");
        String eventId = scanner.nextLine();

        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        System.out.println("\n--- CANCEL EVENT ---");
        displayEventSummary(event);
        System.out.println("Current Status: " + event.getStatus());

        if (event.getStatus().equals("Cancelled")) {
            System.out.println("⚠️ This event is already cancelled.");
            return;
        }

        System.out.print("\nType 'YES' to confirm cancellation: ");
        String confirm = scanner.nextLine();

        if (confirm.equals("YES")) {
            event.cancelEvent();  // Sets status to "Cancelled"
            System.out.println("Event has been CANCELLED!");
            System.out.println("No new bookings can be made for this event.");
            System.out.println("All existing bookings have been cancelled and waitlist cleared.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    // View Event Roster

    private void viewEventRoster() {
        System.out.print("\nEnter Event ID: ");
        String eventId = scanner.nextLine();

        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ROSTER FOR: " + event.getTitle());
        System.out.println("Date: " + event.getDateTime() + " | Location: " + event.getLocation());
        System.out.println("Capacity: " + event.getCapacity() + " | Status: " + event.getStatus());
        System.out.println("=".repeat(60));

        // For now, show placeholder since Booking/Waitlist not ready
        System.out.println("\nCONFIRMED LIST (0/" + event.getCapacity() + "):");
        System.out.println("  (Booking system coming soon)");

        System.out.println("\nWAITLIST:");
        System.out.println("  (Waitlist system coming soon)");


    }

    // Search By Title

    private void searchByTitle() {
        System.out.print("\nEnter title to search (partial matches allowed): ");
        String searchTerm = scanner.nextLine().toLowerCase();

        ArrayList<Event> results = new ArrayList<>();
        for (Event e : events) {
            if (e.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(e);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No events found matching: \"" + searchTerm + "\"");
        } else {
            System.out.println("\n--- Search Results for \"" + searchTerm + "\" ---");
            displayEventList(results);
        }
    }

    // Filter by Type

    private void filterByType() {
        System.out.println("\nSelect event type:");
        System.out.println("1. Workshop");
        System.out.println("2. Seminar");
        System.out.println("3. Concert");
        System.out.print("Choice: ");

        int type = getInt();
        String typeName = "";

        switch (type) {
            case 1: typeName = "Workshop"; break;
            case 2: typeName = "Seminar"; break;
            case 3: typeName = "Concert"; break;
            default:
                System.out.println("Invalid type");
                return;
        }

        ArrayList<Event> results = new ArrayList<>();
        for (Event e : events) {
            if (e.getClass().getSimpleName().equals(typeName)) {
                results.add(e);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No " + typeName + " events found.");
        } else {
            System.out.println("\n--- " + typeName + " Events ---");
            displayEventList(results);
        }
    }

    // List All Events

    private void listAllEvents() {
        if (events.isEmpty()) {
            System.out.println("\nNo events found.");
            return;
        }

        System.out.println("\n--- ALL EVENTS ---");
        displayEventList(events);
    }

    // Helpers

    private Event findEventById(String id) {
        for (Event e : events) {
            if (e.getEventId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    private void displayEventSummary(Event e) {
        System.out.println("ID: " + e.getEventId());
        System.out.println("Title: " + e.getTitle());
        System.out.println("Type: " + e.getEventType());
        System.out.println("When: " + e.getDateTime());
        System.out.println("Where: " + e.getLocation());
        System.out.println("Capacity: " + e.getCapacity());
        System.out.println("Status: " + e.getStatus());
    }

    private void displayEventList(ArrayList<Event> eventList) {
        System.out.println("\n" + "-".repeat(80));
        System.out.printf("%-6s %-20s %-16s %-15s %-5s %-8s %s\n",
                "ID", "Title", "Date", "Location", "Cap", "Status", "Type");
        System.out.println("-".repeat(80));

        for (Event e : eventList) {
            System.out.printf("%-6s %-20s %-16s %-15s %-5d %-8s %s\n",
                    e.getEventId(),
                    truncate(e.getTitle(), 20),
                    truncate(e.getDateTime(), 16),
                    truncate(e.getLocation(), 15),
                    e.getCapacity(),
                    e.getStatus(),
                    e.getEventType());
        }
    }

    private String truncate(String s, int len) {
        if (s == null) return "";
        if (s.length() <= len) return s;
        return s.substring(0, len-3) + "...";
    }

    private int getInt() {
        try {
            int num = scanner.nextInt();
            scanner.nextLine();
            return num;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}