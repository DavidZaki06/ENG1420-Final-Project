package UI;

import Model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MainUI extends Application {

    private BorderPane root; // Main Layout
    private StackPane contentArea; // Area that will hold current panel

    // Create Panels
    private UserUI userUI;
    private EventUI eventUI;
    private BookingUI bookingUI;
    private WaitlistUI waitlistUI;

    // Shared data
    private ArrayList<User> users;
    private ArrayList<Event> events;
    private ArrayList<Booking> bookings;

    @Override
    public void start(Stage primaryStage) {
        // Initialize shared data
        users = new ArrayList<>();
        events = new ArrayList<>();
        bookings = new ArrayList<>();

        // Initialize panels
        userUI = new UserUI();
        eventUI = new EventUI();
        bookingUI = new BookingUI();
        waitlistUI = new WaitlistUI();

        // Pass data to other UI
        userUI.setUsers(users);
        eventUI.setEvents(events);
        bookingUI.setUsers(users);
        bookingUI.setEvents(events);
        bookingUI.setBookings(bookings);
        waitlistUI.setEvents(events);
        waitlistUI.setBookings(bookings);

        //Creates layout
        root = new BorderPane();

        // Create left navigation menu
        VBox navMenu = createNavigationMenu();
        root.setLeft(navMenu);

        // Create content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        contentArea.setPadding(new Insets(20));
        root.setCenter(contentArea);

        // Show User Management by default
        showUserManagement();

        // Creates and shows window
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Campus Event Booking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create Nav Menu, left side panel
    private VBox createNavigationMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(200);
        menu.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("Main Menu");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button userBtn = createNavButton("User Management");
        Button eventBtn = createNavButton("Event Management");
        Button bookingBtn = createNavButton("Booking Management");
        Button waitlistBtn = createNavButton("Waitlist Management");
        Button exitBtn = createNavButton("Exit");

        userBtn.setOnAction(e -> showUserManagement());
        eventBtn.setOnAction(e -> showEventManagement());
        bookingBtn.setOnAction(e -> showBookingManagement());
        waitlistBtn.setOnAction(e -> showWaitlistManagement());
        exitBtn.setOnAction(e -> System.exit(0));

        menu.getChildren().addAll(title, userBtn, eventBtn, bookingBtn, waitlistBtn, exitBtn);
        return menu;
    }

    // Initialize Nav Menu Buttons
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        // Mouse hover colour change
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #3d566e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        return btn;
    }

    private void showUserManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(userUI.getView());
        userUI.refresh();
    }

    private void showEventManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(eventUI.getView());
        eventUI.refresh();
    }

    private void showBookingManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(bookingUI.getView());
        bookingUI.refresh();
    }

    private void showWaitlistManagement() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(waitlistUI.getView());
        waitlistUI.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}