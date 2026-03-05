package UI;

import java.util.Scanner;

public class MainUI {
    private Scanner scanner;
    private EventUI eventUI;
    private UserUI userUI;
    private BookingUI bookingUI;
    private WaitlistUI waitlistUI;

 
    public MainUI() {
        scanner = new Scanner(System.in);
        eventUI = new EventUI(scanner);
        userUI = new UserUI(scanner);
        bookingUI = new BookingUI(scanner);
        waitlistUI = new WaitlistUI(scanner);
    }

    
    public void start() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     CAMPUS EVENT BOOKING SYSTEM");
            System.out.println("=".repeat(60));
            System.out.println("1. User Management");
            System.out.println("2. Event Management");
            System.out.println("3. Booking Management");
            System.out.println("4. Waitlist Management");
            System.out.println("5. Exit");
            System.out.println("-".repeat(60));
            System.out.print("Enter choice: ");

            int choice = getInt();

            switch (choice) {
                case 1: userUI.showMenu(); break;
                case 2: eventUI.showMenu(); break;
                case 3: bookingUI.showMenu(); break;
                case 4: waitlistUI.showMenu(); break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
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


    public static void main(String[] args) {

        MainUI ui = new MainUI();  // Create instance
        ui.start();                 // Call method on instance
    }
}
