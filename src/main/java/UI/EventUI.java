package UI;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.ArrayList;

public class EventUI {
    private BorderPane view;
    private TableView<Event> tableView;
    private ObservableList<Event> eventData;
    private ArrayList<Event> events;

    private TextField searchField;
    private ComboBox<String> filterCombo;

    public EventUI() {
        this.events = new ArrayList<>();
        this.eventData = FXCollections.observableArrayList();

        initialize();
        refreshTable();
    }

    // Layout of Event Manager
    private void initialize() {
        view = new BorderPane();
        view.setPadding(new Insets(10));

        // Top: Title and search/filter
        VBox top = new VBox(10);

        Label titleLabel = new Label("Event Management");

        // Search Bar
        HBox searchBar = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Search by title..."); // Grey hint text

        // Filter Dropdown
        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Workshop", "Seminar", "Concert");
        filterCombo.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterEvents());

        searchBar.getChildren().addAll(new Label("Title:"), searchField,
                new Label("Type:"), filterCombo, searchBtn);

        top.getChildren().addAll(titleLabel, searchBar);
        view.setTop(top);

        // Table
        setupTable();

        // Buttons
        setupButtonPanel();
    }

        // Layout of Table
    private void setupTable() {
        tableView = new TableView<>();

        TableColumn<Event, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEventId()));

        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Event, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEventType()));

        TableColumn<Event, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateTime()));

        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocation()));

        TableColumn<Event, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        tableView.getColumns().addAll(idCol, titleCol, typeCol, dateCol, locationCol, capacityCol, statusCol);
        tableView.setItems(eventData);

        // Double click to view roster
        tableView.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showRoster(row.getItem());
                }
            });
            return row;
        });

        view.setCenter(tableView);
    }

    // Button Panel
    private void setupButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10, 0, 0, 0));

        Button createBtn = new Button("Create Event");
        Button updateBtn = new Button("Update Event");
        Button cancelBtn = new Button("Cancel Event");
        Button rosterBtn = new Button("View Roster");
        Button refreshBtn = new Button("Refresh");

        createBtn.setOnAction(e -> showCreateDialog());
        updateBtn.setOnAction(e -> showUpdateDialog());
        cancelBtn.setOnAction(e -> cancelEvent());
        rosterBtn.setOnAction(e -> {
            Event selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRoster(selected);
            } else {
                showAlert("No Selection", "Please select an event.");
            }
        });
        refreshBtn.setOnAction(e -> refreshTable());

        buttonPanel.getChildren().addAll(createBtn, updateBtn, cancelBtn, rosterBtn, refreshBtn);
        view.setBottom(buttonPanel);
    }

    // Create Event Button Logic
    private void showCreateDialog() {
        Dialog<Event> dialog = new Dialog<>(); // Pop-up Window
        dialog.setTitle("Create Event");
        dialog.setHeaderText("Enter event details");

        ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField idField = new TextField();  // Textboxes for Event Info
        TextField titleField = new TextField();
        TextField dateField = new TextField();
            dateField.setPromptText("YYYY-MM-DD HH:MM");
        TextField locationField = new TextField();
        TextField capacityField = new TextField();

        ComboBox<String> typeCombo = new ComboBox<>(); // Selection Dropdown
        typeCombo.getItems().addAll("Workshop", "Seminar", "Concert");
        typeCombo.setValue("Workshop");

        TextField specificField = new TextField();
        Label specificLabel = new Label("Topic:");

        typeCombo.setOnAction(e -> { // Prompt User for Restriction
            String type = typeCombo.getValue();
            if (type.equals("Workshop")) {
                specificLabel.setText("Topic:");
                specificField.setPromptText("Enter topic");
            } else if (type.equals("Seminar")) {
                specificLabel.setText("Speaker:");
                specificField.setPromptText("Enter speaker name");
            } else {
                specificLabel.setText("Age Restriction:");
                specificField.setPromptText("Enter age restriction");
            }
        });

        // Grid with Selections
        int row = 0;
        grid.add(new Label("Event ID:"), 0, row);
        grid.add(idField, 1, row++);
        grid.add(new Label("Title:"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(new Label("Date/Time:"), 0, row);
        grid.add(dateField, 1, row++);
        grid.add(new Label("Location:"), 0, row);
        grid.add(locationField, 1, row++);
        grid.add(new Label("Capacity:"), 0, row);
        grid.add(capacityField, 1, row++);
        grid.add(new Label("Type:"), 0, row);
        grid.add(typeCombo, 1, row++);
        grid.add(specificLabel, 0, row);
        grid.add(specificField, 1, row);

        dialog.getDialogPane().setContent(grid);

        // When Create Button pressed, validates entries
        dialog.setResultConverter(button -> {
            if (button == createButton) {
                if (idField.getText().trim().isEmpty() ||
                        titleField.getText().trim().isEmpty() ||
                        dateField.getText().trim().isEmpty() ||
                        locationField.getText().trim().isEmpty() ||
                        capacityField.getText().trim().isEmpty() ||
                        specificField.getText().trim().isEmpty()) {
                    showAlert("Error", "All fields required!");
                    return null;
                }

                // Check duplicate
                for (Event e : events) {
                    if (e.getEventId().equals(idField.getText().trim())) {
                        showAlert("Error", "Event ID already exists!");
                        return null;
                    }
                }

                // Validate capacity
                int capacity;
                try {
                    capacity = Integer.parseInt(capacityField.getText().trim());
                    if (capacity <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Capacity must greater than 0!");
                    return null;
                }

                String type = typeCombo.getValue();
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                String date = dateField.getText().trim();
                String location = locationField.getText().trim();
                String specific = specificField.getText().trim();

                // Add event to array
                switch (type) {
                    case "Workshop":
                        return new Workshop(id, title, date, location, capacity, "Active", specific);
                    case "Seminar":
                        return new Seminar(id, title, date, location, capacity, "Active", specific);
                    case "Concert":
                        return new Concert(id, title, date, location, capacity, "Active", specific);
                    default:
                        return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newEvent -> {
            events.add(newEvent);
            refreshTable();
            showAlert("Success", "Event created!");
        });
    }

    // Method for Update Button

    private void showUpdateDialog() {
        Event selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an event to update.");
            return;
        }

        Dialog<Event> dialog = new Dialog<>(); // Pop out Window
        dialog.setTitle("Update Event");
        dialog.setHeaderText("Update: " + selected.getTitle());

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(selected.getTitle());
        TextField dateField = new TextField(selected.getDateTime());
        TextField locationField = new TextField(selected.getLocation());
        TextField capacityField = new TextField(String.valueOf(selected.getCapacity()));

        int row = 0;
        grid.add(new Label("Title:"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(new Label("Date/Time:"), 0, row);
        grid.add(dateField, 1, row++);
        grid.add(new Label("Location:"), 0, row);
        grid.add(locationField, 1, row++);
        grid.add(new Label("Capacity:"), 0, row);
        grid.add(capacityField, 1, row++);

        // Type-specific field
        if (selected instanceof Workshop) {
            Workshop w = (Workshop) selected;
            TextField topicField = new TextField(w.getTopic());
            grid.add(new Label("Topic:"), 0, row);
            grid.add(topicField, 1, row);

            dialog.setResultConverter(button -> {
                if (button == updateButton) {
                    w.setTitle(titleField.getText());
                    w.setDateTime(dateField.getText());
                    w.setLocation(locationField.getText());
                    w.setCapacity(Integer.parseInt(capacityField.getText()));
                    w.setTopic(topicField.getText());
                    return w;
                }
                return null;
            });
        } else if (selected instanceof Seminar) {
            Seminar s = (Seminar) selected;
            TextField speakerField = new TextField(s.getSpeakerName());
            grid.add(new Label("Speaker:"), 0, row);
            grid.add(speakerField, 1, row);

            dialog.setResultConverter(button -> {
                if (button == updateButton) {
                    s.setTitle(titleField.getText());
                    s.setDateTime(dateField.getText());
                    s.setLocation(locationField.getText());
                    s.setCapacity(Integer.parseInt(capacityField.getText()));
                    s.setSpeakerName(speakerField.getText());
                    return s;
                }
                return null;
            });
        } else if (selected instanceof Concert) {
            Concert c = (Concert) selected;
            TextField ageField = new TextField(c.getAgeRestriction());
            grid.add(new Label("Age Restriction:"), 0, row);
            grid.add(ageField, 1, row);

            dialog.setResultConverter(button -> {
                if (button == updateButton) {
                    c.setTitle(titleField.getText());
                    c.setDateTime(dateField.getText());
                    c.setLocation(locationField.getText());
                    c.setCapacity(Integer.parseInt(capacityField.getText()));
                    c.setAgeRestriction(ageField.getText());
                    return c;
                }
                return null;
            });
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(updated -> {
            refreshTable();
            showAlert("Success", "Event updated!");
        });
    }

    private void cancelEvent() {
        Event selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an event to cancel.");
            return;
        }

        if (selected.getStatus().equals("Cancelled")) {
            showAlert("Already Cancelled", "This event is already cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText("Cancel Event: " + selected.getTitle());
        confirm.setContentText("This will cancel all bookings and clear the waitlist. Continue?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selected.cancelEvent();
                refreshTable();
                showAlert("Success", "Event cancelled!");
            }
        });
    }

    private void showRoster(Event event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Roster");
        dialog.setHeaderText(event.getTitle() + " - " + event.getDateTime());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Event info
        Label infoLabel = new Label(
                "Location: " + event.getLocation() +
                        " | Capacity: " + event.getCapacity() +
                        " | Status: " + event.getStatus()
        );
        content.getChildren().add(infoLabel);

        // Confirmed list
        Label confirmedLabel = new Label("CONFIRMED LIST:");
        confirmedLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(confirmedLabel);

        ListView<String> confirmedList = new ListView<>();
        if (event.getConfirmedBookings().isEmpty()) {
            confirmedList.getItems().add("No confirmed bookings");
        } else {
            for (Booking b : event.getConfirmedBookings()) {
                confirmedList.getItems().add(b.getUserId() + " - " + b.getBookingId());
            }
        }
        confirmedList.setPrefHeight(100);
        content.getChildren().add(confirmedList);

        // Waitlist
        Label waitlistLabel = new Label("WAITLIST:");
        waitlistLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(waitlistLabel);

        ListView<String> waitlistList = new ListView<>();
        if (event.getWaitlist().isEmpty()) {
            waitlistList.getItems().add("No waitlisted users");
        } else {
            for (Booking b : event.getWaitlist()) {
                waitlistList.getItems().add(b.getUserId() + " - " + b.getBookingId());
            }
        }
        waitlistList.setPrefHeight(100);
        content.getChildren().add(waitlistList);

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // Search and Filter Events

    private void filterEvents() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterCombo.getValue();

        ArrayList<Event> filtered = new ArrayList<>();

        // Check if Search or Filter have been applied
        for (Event e : events) {
            boolean matchesSearch = searchText.isEmpty() ||
                    e.getTitle().toLowerCase().contains(searchText);
            boolean matchesType = filterType.equals("All") ||
                    e.getEventType().equals(filterType);

            if (matchesSearch && matchesType) {
                filtered.add(e);
            }
        }

        eventData.clear();
        eventData.addAll(filtered);
    }

    private void refreshTable() {
        eventData.clear();
        eventData.addAll(events);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setEvents(ArrayList<Event> eventList) {
        this.events = eventList;
        refreshTable();
    }

    public Node getView() {
        return view;
    }

    public void refresh() {
        refreshTable();
    }
}