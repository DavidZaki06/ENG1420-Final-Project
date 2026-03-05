package UI;

import java.util.ArrayList;
import java.util.Scanner;

// Simple internal User class - delete once user method provided
class User {
    String userId;
    String name;
    String email;
    String type; // Student, Staff, Guest

    User(String id, String name, String email, String type) {
        this.userId = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }
}

public class UserUI {
    private Scanner scanner;
    private ArrayList<User> users;  

    public UserUI(Scanner s) {
        this.scanner = s;
        this.users = new ArrayList<>();

    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("          USER MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("1. Add User");
            System.out.println("2. View User Details");
            System.out.println("3. List All Users");
            System.out.println("4. Back to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("Choice: ");

            int choice = getInt();

            switch (choice) {
                case 1: addUser(); break;
                case 2: viewUserDetails(); break;
                case 3: listAllUsers(); break;
                case 4: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    private void addUser() {
        System.out.println("\n--- ADD USER ---");

        System.out.println("Select user type:");
        System.out.println("1. Student");
        System.out.println("2. Staff");
        System.out.println("3. Guest");
        System.out.print("Choice: ");
        int type = getInt();

        String typeStr = type == 1 ? "Student" : type == 2 ? "Staff" : type == 3 ? "Guest" : null;
        if (typeStr == null) {
            System.out.println("Invalid type");
            return;
        }

        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        // Check for duplicate ID
        for (User u : users) {
            if (u.userId.equals(userId)) {
                System.out.println("User ID already exists!");
                return;
            }
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        users.add(new User(userId, name, email, typeStr));
        System.out.println("User added successfully!");
    }

    private void viewUserDetails() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine();

        User user = null;
        for (User u : users) {
            if (u.userId.equals(userId)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        int limit = user.type.equals("Student") ? 3 : user.type.equals("Staff") ? 5 : 1;

        System.out.println("\n--- USER DETAILS ---");
        System.out.println("ID: " + user.userId);
        System.out.println("Name: " + user.name);
        System.out.println("Email: " + user.email);
        System.out.println("Type: " + user.type);
        System.out.println("Booking Limit: " + limit);

    }

    private void listAllUsers() {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }

        System.out.println("\n--- ALL USERS ---");
        System.out.printf("%-6s %-20s %-25s %-10s\n",
                "ID", "Name", "Email", "Type");
        System.out.println("-".repeat(65));

        for (User u : users) {
            System.out.printf("%-6s %-20s %-25s %-10s\n",
                    u.userId,
                    truncate(u.name, 20),
                    truncate(u.email, 25),
                    u.type);
        }
    }

    private String truncate(String s, int len) {
        if (s == null || s.length() <= len) return s;
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
