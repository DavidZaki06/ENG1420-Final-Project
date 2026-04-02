package UI;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingUI {
    private BorderPane view;
    private TableView<Booking> tableView;
    private ObservableList<Booking> bookingData;
    private ArrayList<Booking> bookings;

    // References to users and events from other panels
    private ArrayList<User> users;
    private ArrayList<Event> events;

    public BookingUI() {
        this.bookings = new ArrayList<>();
        this.bookingData = FXCollections.observableArrayList();
        this.users = new ArrayList<>();
        this.events = new ArrayList<>();

        initialize();
        refreshTable();
    }


    // Connects this UI to the shared users list from MainUI
    public void setUsers(ArrayList<User> userList) {
        this.users = userList;
    }


    // Connects this UI to the shared events list from MainUI
    public void setEvents(ArrayList<Event> eventList) {
        this.events = eventList;
    }


    // Sets up the main layout for the Booking Management panel
    private void initialize() {
        view = new BorderPane();
        view.setPadding(new Insets(10));

        Label titleLabel = new Label("Booking Management");
        view.setTop(titleLabel);

        setupTable();
        setupButtonPanel();
    }


    // Table View
    private void setupTable() {
        tableView = new TableView<>();

        // Booking ID column
        TableColumn<Booking, String> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingId()));

        // User column - shows the user's name
        TableColumn<Booking, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserId().getName()));

        // Event column - shows the event title
        TableColumn<Booking, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEvent().getTitle()));

        // Status column (Confirmed/Waitlisted/Cancelled)
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingStatus().toString()));

        // Creation date column
        TableColumn<Booking, String> dateCol = new TableColumn<>("Created");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt().toString().substring(0, 16)));

        tableView.getColumns().addAll(idCol, userCol, eventCol, statusCol, dateCol);
        tableView.setItems(bookingData);
        view.setCenter(tableView);
    }

    // Button Panel
    private void setupButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10, 0, 0, 0));

        Button bookBtn = new Button("Book Event");
        Button cancelBtn = new Button("Cancel Booking");
        Button refreshBtn = new Button("Refresh");

        bookBtn.setOnAction(e -> showBookEventDialog());
        cancelBtn.setOnAction(e -> showCancelBookingDialog());
        refreshBtn.setOnAction(e -> refreshTable());

        buttonPanel.getChildren().addAll(bookBtn, cancelBtn, refreshBtn);
        view.setBottom(buttonPanel);
    }

    /**
     * Dialog for creating a new booking
     * Handles capacity checks, waitlist placement, and booking limits
     */
    private void showBookEventDialog() {
        if (users.isEmpty() || events.isEmpty()) {
            showAlert("Error", "No users or events available. Please add them first.");
            return;
        }

        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Book Event");
        dialog.setHeaderText("Create new booking");

        ButtonType bookButton = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButton, ButtonType.CANCEL);

        // Form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Booking ID input
        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("e.g., B001");

        // User dropdown
        ComboBox<User> userCombo = new ComboBox<>();
        userCombo.getItems().addAll(users);
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? null : user.getUserId() + " - " + user.getName() + " (" + user.getUserType() + ")");
            }
        });
        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? null : user.getUserId() + " - " + user.getName());
            }
        });

        // Event dropdown with capacity info
        ComboBox<Event> eventCombo = new ComboBox<>();
        eventCombo.getItems().addAll(events);
        eventCombo.setCellFactory(lv -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty ? null : event.getEventId() + " - " + event.getTitle() +
                        " (" + event.getConfirmedBookings().size() + "/" + event.getCapacity() + ")");
            }
        });
        eventCombo.setButtonCell(new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty ? null : event.getEventId() + " - " + event.getTitle());
            }
        });

        // Arrange fields in grid
        int row = 0;
        grid.add(new Label("Booking ID:"), 0, row);
        grid.add(bookingIdField, 1, row++);
        grid.add(new Label("Select User:"), 0, row);
        grid.add(userCombo, 1, row++);
        grid.add(new Label("Select Event:"), 0, row);
        grid.add(eventCombo, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // Handle book button click
        dialog.setResultConverter(button -> {
            if (button == bookButton) {
                // Validate booking ID
                if (bookingIdField.getText().trim().isEmpty()) {
                    showAlert("Error", "Booking ID required!");
                    return null;
                }

                // Check for duplicate booking ID
                for (Booking b : bookings) {
                    if (b.getBookingId().equals(bookingIdField.getText().trim())) {
                        showAlert("Error", "Booking ID already exists!");
                        return null;
                    }
                }

                User selectedUser = userCombo.getValue();
                Event selectedEvent = eventCombo.getValue();

                if (selectedUser == null || selectedEvent == null) {
                    showAlert("Error", "Please select user and event.");
                    return null;
                }

                // Check if event is active
                if (!selectedEvent.getStatus().equals("Active")) {
                    showAlert("Error", "Cannot book - event is cancelled.");
                    return null;
                }

                // Check if user already booked this event
                BookingService service = new BookingService();
                if (service.alreadyBooked(selectedUser, selectedEvent)) {
                    showAlert("Error", "User already has a booking for this event.");
                    return null;
                }

                // Check user's booking limit
                int confirmedCount = 0;
                for (Booking b : bookings) {
                    if (b.getUserId().equals(selectedUser) && b.getBookingStatus() == BookingStatus.CONFIRMED) {
                        confirmedCount++;
                    }
                }

                int limit = selectedUser.getBookingLimit();
                if (confirmedCount >= limit) {
                    showAlert("Error", selectedUser.getUserType() + " booking limit reached (" + limit + ")");
                    return null;
                }

                // Create new booking
                Booking newBooking = new Booking(bookingIdField.getText().trim(), selectedUser, selectedEvent, LocalDateTime.now());

                // Determine if confirmed or waitlisted based on capacity
                int availableCapacity = selectedEvent.getCapacity() - selectedEvent.getConfirmedBookings().size();

                if (availableCapacity > 0) {
                    newBooking.setStatus(BookingStatus.CONFIRMED);
                    selectedEvent.addConfirmedBooking(newBooking);
                } else {
                    newBooking.setStatus(BookingStatus.WAITLISTED);
                    selectedEvent.addToWaitlist(newBooking);
                }

                return newBooking;
            }
            return null;
        });

        // Add booking if creation was successful
        dialog.showAndWait().ifPresent(newBooking -> {
            bookings.add(newBooking);
            refreshTable();

            if (newBooking.getBookingStatus() == BookingStatus.CONFIRMED) {
                showAlert("Success", "Booking CONFIRMED!");
            } else {
                // Calculate waitlist position
                int position = 0;
                for (Booking b : newBooking.getEvent().getWaitlist()) {
                    position++;
                    if (b.equals(newBooking)) break;
                }
                showAlert("Waitlisted", "Event is full. You have been waitlisted (Position: " + position + ")");
            }
        });
    }

    /**
     * Dialog for canceling an existing booking
     * Automatically promotes the first waitlisted user if applicable
     */
    private void showCancelBookingDialog() {
        Booking selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a booking to cancel.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel Booking: " + selected.getBookingId());
        confirm.setContentText("Are you sure you want to cancel this booking?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Event event = selected.getEvent();
                boolean wasConfirmed = selected.isConfirmed();

                // Cancel the booking using event's method
                event.cancelABooking(selected);

                // Update the booking in the local list
                for (int i = 0; i < bookings.size(); i++) {
                    if (bookings.get(i).getBookingId().equals(selected.getBookingId())) {
                        bookings.set(i, selected);
                        break;
                    }
                }

                refreshTable();
                showAlert("Success", "Booking cancelled!");

                // Check if cancellation triggered a waitlist promotion
                if (wasConfirmed && !event.getWaitlist().isEmpty()) {
                    // Find the promoted booking
                    Booking promoted = null;
                    for (Booking b : event.getConfirmedBookings()) {
                        if (b.getBookingStatus() == BookingStatus.CONFIRMED) {
                            promoted = b;
                        }
                    }

                    if (promoted != null) {
                        Alert promotionAlert = new Alert(Alert.AlertType.INFORMATION);
                        promotionAlert.setTitle("Promotion Notification");
                        promotionAlert.setHeaderText("AUTOMATIC PROMOTION!");
                        promotionAlert.setContentText(
                                "User " + promoted.getUserId().getName() +
                                        " has been promoted from waitlist to CONFIRMED for event: " +
                                        event.getTitle()
                        );
                        promotionAlert.showAndWait();
                    }
                }
            }
        });
    }


    // Connects this UI to the shared bookings list from MainUI
    public void setBookings(ArrayList<Booking> bookingList) {
        this.bookings = bookingList;
        refreshTable();
    }


    // Refreshes the table with current booking data
    private void refreshTable() {
        bookingData.clear();
        bookingData.addAll(bookings);
    }


    // Shows a simple alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Node getView() {
        return view;
    }

    public void refresh() {
        refreshTable();
    }
}
