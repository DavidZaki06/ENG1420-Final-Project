package UI;

import java.util.ArrayList;
import java.util.Scanner;

// Simple internal Booking class - delete once booking method provided
class Booking {
    String bookingId;
    String userId;
    String eventId;
    String status; // Confirmed, Waitlisted, Cancelled
    String createdAt;

    Booking(String id, String uid, String eid, String status, String created) {
        this.bookingId = id;
        this.userId = uid;
        this.eventId = eid;
        this.status = status;
        this.createdAt = created;
    }
}

public class BookingUI {
    private Scanner scanner;
    private ArrayList<Booking> bookings;  

    public BookingUI(Scanner s) {
        this.scanner = s;
        this.bookings = new ArrayList<>();

        // Sample data
        bookings.add(new Booking("B001", "U001", "C001", "Confirmed", "2024-03-15 10:30"));
        bookings.add(new Booking("B002", "U002", "C001", "Waitlisted", "2024-03-15 11:45"));
        bookings.add(new Booking("B003", "U001", "S001", "Confirmed", "2024-03-16 09:20"));
        bookings.add(new Booking("B004", "U003", "C001", "Waitlisted", "2024-03-16 14:30"));
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("          BOOKING MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("1. Book an Event");
            System.out.println("2. Cancel a Booking");
            System.out.println("3. View User's Bookings");
            System.out.println("4. Back to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("Choice: ");

            int choice = getInt();

            switch (choice) {
                case 1: bookEvent(); break;
                case 2: cancelBooking(); break;
                case 3: viewUserBookings(); break;
                case 4: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    private void bookEvent() {
        System.out.println("\n--- BOOK EVENT ---");

        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();

        // Check duplicate
        for (Booking b : bookings) {
            if (b.bookingId.equals(bookingId)) {
                System.out.println("Booking ID already exists!");
                return;
            }
        }

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter Event ID: ");
        String eventId = scanner.nextLine();

        // Capacity Check
        System.out.println("\nSelect result (for demo):");
        System.out.println("1. Confirmed (event has space)");
        System.out.println("2. Waitlisted (event is full)");
        System.out.print("Choice: ");
        int result = getInt();

        String status = (result == 1) ? "Confirmed" : "Waitlisted";
        String createdAt = java.time.LocalDateTime.now().toString().substring(0, 16);

        bookings.add(new Booking(bookingId, userId, eventId, status, createdAt));

        if (status.equals("Confirmed")) {
            System.out.println("Booking CONFIRMED!");
        } else {
            System.out.println("Event is full. You have been WAITLISTED.");
        }
    }

    private void cancelBooking() {
        System.out.print("\nEnter Booking ID to cancel: ");
        String bookingId = scanner.nextLine();

        // Find the booking
        Booking toCancel = null;
        for (Booking b : bookings) {
            if (b.bookingId.equals(bookingId)) {
                toCancel = b;
                break;
            }
        }

        if (toCancel == null) {
            System.out.println("Booking not found!");
            return;
        }

        String eventId = toCancel.eventId;
        boolean wasConfirmed = toCancel.status.equals("Confirmed");

        // Cancel the booking
        toCancel.status = "Cancelled";
        System.out.println("Booking cancelled!");

        // If it was confirmed, promote from waitlist
        if (wasConfirmed) {
            // Find first waitlisted booking for same event
            for (Booking b : bookings) {
                if (b.eventId.equals(eventId) && b.status.equals("Waitlisted")) {
                    b.status = "Confirmed";
                    System.out.println("User " + b.userId + " promoted from waitlist!");
                    break;
                }
            }
        }
    }

    private void viewUserBookings() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine();

        ArrayList<Booking> userBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.userId.equals(userId)) {
                userBookings.add(b);
            }
        }

        if (userBookings.isEmpty()) {
            System.out.println("No bookings found for user: " + userId);
            return;
        }

        System.out.println("\n--- BOOKINGS FOR USER " + userId + " ---");
        System.out.printf("%-8s %-8s %-12s %s\n",
                "ID", "Event", "Status", "Date");
        System.out.println("-".repeat(40));

        for (Booking b : userBookings) {
            System.out.printf("%-8s %-8s %-12s %s\n",
                    b.bookingId,
                    b.eventId,
                    b.status,
                    b.createdAt.substring(0, 10));
        }
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
