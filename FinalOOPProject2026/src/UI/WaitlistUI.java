package UI;

import java.util.ArrayList;
import java.util.Scanner;

// Simple internal WaitlistEntry class - delete once waitlist method provided
class WaitlistEntry {
    String eventId;
    String userId;
    String bookingId;
    int position;
    String createdAt;

    WaitlistEntry(String eid, String uid, String bid, int pos, String created) {
        this.eventId = eid;
        this.userId = uid;
        this.bookingId = bid;
        this.position = pos;
        this.createdAt = created;
    }
}

public class WaitlistUI {
    private Scanner scanner;
    private ArrayList<WaitlistEntry> waitlist;  

    public WaitlistUI(Scanner s) {
        this.scanner = s;
        this.waitlist = new ArrayList<>();

    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("          WAITLIST MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("1. View Event Waitlist");
            System.out.println("2. Remove from Waitlist");
            System.out.println("3. Back to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("Choice: ");

            int choice = getInt();

            switch (choice) {
                case 1: viewWaitlist(); break;
                case 2: removeFromWaitlist(); break;
                case 3: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    private void viewWaitlist() {
        System.out.print("\nEnter Event ID: ");
        String eventId = scanner.nextLine();

        ArrayList<WaitlistEntry> eventWaitlist = new ArrayList<>();
        for (WaitlistEntry w : waitlist) {
            if (w.eventId.equals(eventId)) {
                eventWaitlist.add(w);
            }
        }

        System.out.println("\n--- WAITLIST FOR EVENT: " + eventId + " ---");

        if (eventWaitlist.isEmpty()) {
            System.out.println("Waitlist is empty.");
            return;
        }

        System.out.printf("%-6s %-10s %-8s %s\n",
                "Pos", "User ID", "Booking", "Added");
        System.out.println("-".repeat(40));

        for (WaitlistEntry w : eventWaitlist) {
            System.out.printf("%-6d %-10s %-8s %s\n",
                    w.position,
                    w.userId,
                    w.bookingId,
                    w.createdAt);
        }
    }

    private void removeFromWaitlist() {
        System.out.print("\nEnter Event ID: ");
        String eventId = scanner.nextLine();

        System.out.print("Enter User ID to remove: ");
        String userId = scanner.nextLine();

        // Find and remove the entry
        WaitlistEntry toRemove = null;
        for (WaitlistEntry w : waitlist) {
            if (w.eventId.equals(eventId) && w.userId.equals(userId)) {
                toRemove = w;
                break;
            }
        }

        if (toRemove == null) {
            System.out.println("User not found on waitlist for this event.");
            return;
        }

        waitlist.remove(toRemove);

        // Renumber remaining entries for this event
        int newPos = 1;
        for (WaitlistEntry w : waitlist) {
            if (w.eventId.equals(eventId)) {
                w.position = newPos++;
            }
        }

        System.out.println("User removed from waitlist.");
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
