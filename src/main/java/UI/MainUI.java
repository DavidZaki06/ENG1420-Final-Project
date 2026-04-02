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

    // Main layout containers
    private BorderPane root;
    private StackPane contentArea; // This is where different panels will be displayed

    // All the management panels
    private UserUI userUI;
    private EventUI eventUI;
    private BookingUI bookingUI;
    private WaitlistUI waitlistUI;

    // Shared data - these get passed around so all panels see the same info
    private ArrayList<User> users;
    private ArrayList<Event> events;
    private ArrayList<Booking> bookings;

    @Override
    public void start(Stage primaryStage) {

        // Replaced empty initialization with Model.FileLoader
        // Load data from CSV files
        users = FileLoader.loadUsers("users.csv");
        events = FileLoader.loadEvents("events.csv");

// If files don't exist, start with empty lists
        if (users == null) users = new ArrayList<>();
        if (events == null) events = new ArrayList<>();

// Load bookings (needs users and events lists)
        bookings = FileLoader.loadBookings("bookings.csv", users, events);
        if (bookings == null) bookings = new ArrayList<>();

        // Create all the panels
        userUI = new UserUI();
        eventUI = new EventUI();
        bookingUI = new BookingUI();
        waitlistUI = new WaitlistUI();

        // Connect everything so they share the same data
        userUI.setUsers(users);
        
        eventUI.setEvents(events);
        
        bookingUI.setUsers(users);
        bookingUI.setEvents(events);
        bookingUI.setBookings(bookings);
        
        waitlistUI.setEvents(events);
        waitlistUI.setBookings(bookings);

        // Set up the main window layout
        root = new BorderPane();

        // Left side - navigation menu
        VBox navMenu = createNavigationMenu();
        root.setLeft(navMenu);

        // Center area - where the actual management panels will show up
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        contentArea.setPadding(new Insets(20));
        root.setCenter(contentArea);

        // Start with User Management when program opens
        showUserManagement();

        // Create and show the window
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Campus Event Booking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);

        String baseStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;";
        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #3d566e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;")
        );

        btn.setOnMouseExited(e -> 
            btn.setStyle(baseStyle)
        );

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
        waitlistUI.refreshEvents();  // Refresh the combo box
        waitlistUI.refresh();        // Refresh the display
    }

    public static void main(String[] args) {
        launch(args);
    }
}
