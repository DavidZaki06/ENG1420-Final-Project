package UI;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WaitlistUI {
    private BorderPane view;
    private ListView<String> waitlistView;
    private ComboBox<Event> eventCombo;
    private Label eventInfoLabel;

    // Need access to events and bookings
    private ArrayList<Event> events;
    private ArrayList<Booking> bookings;

    public WaitlistUI() {
        this.events = new ArrayList<>();
        this.bookings = new ArrayList<>();

        initialize();
    }

    // Method to set events from EventUI
    public void setEvents(ArrayList<Event> eventList) {
        this.events = eventList;
        eventCombo.setItems(FXCollections.observableArrayList(events));
        eventCombo.setCellFactory(lv -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty ? null : event.getEventId() + " - " + event.getTitle());
            }
        });
        eventCombo.setButtonCell(new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(empty ? null : event.getEventId() + " - " + event.getTitle());
            }
        });
    }

    // Method to set bookings from BookingUI
    public void setBookings(ArrayList<Booking> bookingList) {
        this.bookings = bookingList;
    }

    private void initialize() {
        view = new BorderPane();
        view.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label("Waitlist Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Top panel with event selection
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(0, 0, 10, 0));

        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label selectLabel = new Label("Select Event:");
        eventCombo = new ComboBox<>();
        eventCombo.setPrefWidth(300);
        eventCombo.setOnAction(e -> updateWaitlistDisplay());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> updateWaitlistDisplay());

        selectorBox.getChildren().addAll(selectLabel, eventCombo, refreshBtn);

        eventInfoLabel = new Label("No event selected");
        eventInfoLabel.setStyle("-fx-font-style: italic;");

        topPanel.getChildren().addAll(titleLabel, selectorBox, eventInfoLabel);
        view.setTop(topPanel);

        // Center - Waitlist display
        waitlistView = new ListView<>();
        waitlistView.setPrefHeight(400);
        view.setCenter(waitlistView);

        // Bottom - Action buttons
        setupButtonPanel();
    }

    private void setupButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10, 0, 0, 0));
        buttonPanel.setAlignment(javafx.geometry.Pos.CENTER);

        Button viewBtn = new Button("View Waitlist");
        Button removeBtn = new Button("Remove from Waitlist");
        Button refreshBtn = new Button("Refresh");

        viewBtn.setOnAction(e -> updateWaitlistDisplay());
        removeBtn.setOnAction(e -> removeFromWaitlist());
        refreshBtn.setOnAction(e -> updateWaitlistDisplay());

        buttonPanel.getChildren().addAll(viewBtn, removeBtn, refreshBtn);
        view.setBottom(buttonPanel);
    }
    
     // Updates the waitlist display for the selected even
    private void updateWaitlistDisplay() {
        Event selectedEvent = eventCombo.getValue();

        if (selectedEvent == null) {
            eventInfoLabel.setText("No event selected");
            waitlistView.getItems().clear();
            waitlistView.getItems().add("Please select an event to view waitlist.");
            return;
        }

        // Update event info
        String info = String.format("Event: %s | Capacity: %d | Confirmed: %d | Waitlist: %d | Status: %s",
                selectedEvent.getTitle(),
                selectedEvent.getCapacity(),
                selectedEvent.getConfirmedBookings().size(),
                selectedEvent.getWaitlist().size(),
                selectedEvent.getStatus()
        );
        eventInfoLabel.setText(info);

        // Clear and repopulate waitlist
        waitlistView.getItems().clear();

        if (selectedEvent.getWaitlist().isEmpty()) {
            waitlistView.getItems().add("Waitlist is empty.");
            return;
        }

        // Display each waitlisted user with position and timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        int position = 1;

        for (Booking booking : selectedEvent.getWaitlist()) {
            String entry = String.format("%d. %s - %s (Booked: %s)",
                    position++,
                    booking.getUserId().getName(),
                    booking.getUserId().getUserId(),
                    booking.getCreatedAt().format(formatter)
            );
            waitlistView.getItems().add(entry);
        }
    }

    // Removes a user from the waitlist
    private void removeFromWaitlist() {
        Event selectedEvent = eventCombo.getValue();

        if (selectedEvent == null) {
            showAlert("Error", "Please select an event first.");
            return;
        }

        if (selectedEvent.getWaitlist().isEmpty()) {
            showAlert("Info", "Waitlist is empty.");
            return;
        }

        // Create dialog to select user from waitlist
        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Remove from Waitlist");
        dialog.setHeaderText("Select user to remove from waitlist");

        ButtonType removeButton = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButton, ButtonType.CANCEL);

        ListView<String> userList = new ListView<>();
        userList.setPrefHeight(200);

        int position = 1;
        for (Booking booking : selectedEvent.getWaitlist()) {
            String entry = String.format("%d. %s (%s) - %s",
                    position++,
                    booking.getUserId().getName(),
                    booking.getUserId().getUserId(),
                    booking.getCreatedAt().toString().substring(0, 16)
            );
            userList.getItems().add(entry);
        }

        userList.getSelectionModel().select(0);

        dialog.getDialogPane().setContent(userList);

        dialog.setResultConverter(button -> {
            if (button == removeButton) {
                int selectedIndex = userList.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    // Get the actual booking object
                    Booking toRemove = null;
                    int currentPos = 0;
                    for (Booking b : selectedEvent.getWaitlist()) {
                        if (currentPos == selectedIndex) {
                            toRemove = b;
                            break;
                        }
                        currentPos++;
                    }
                    return toRemove;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(bookingToRemove -> {
            if (bookingToRemove != null) {
                // Cancel the booking
                bookingToRemove.cancel();

                // Remove from event's waitlist
                selectedEvent.getWaitlist().remove(bookingToRemove);

                // Update display
                updateWaitlistDisplay();

                showAlert("Success", "User removed from waitlist.\nBooking status updated to CANCELLED.");
            }
        });
    }

    // Method to show promotion notification (called from BookingUI)
    public void showPromotionNotification(Booking promotedBooking) {
        if (promotedBooking == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Promotion Notification");
        alert.setHeaderText("AUTOMATIC PROMOTION!");
        alert.setContentText(String.format(
                "User: %s (%s)\n" +
                        "Event: %s\n" +
                        "Has been promoted from WAITLIST to CONFIRMED!",
                promotedBooking.getUserId().getName(),
                promotedBooking.getUserId().getUserId(),
                promotedBooking.getEvent().getTitle()
        ));

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f0f8ff;");
        dialogPane.setMinHeight(200);

        alert.showAndWait();

        // Refresh display if this event is currently selected
        Event selectedEvent = eventCombo.getValue();
        if (selectedEvent != null && selectedEvent.equals(promotedBooking.getEvent())) {
            updateWaitlistDisplay();
        }
    }

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
        updateWaitlistDisplay();
    }
}
