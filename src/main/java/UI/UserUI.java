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

public class UserUI {
    private BorderPane view;
    private TableView<User> tableView;
    private ObservableList<User> userData;
    private ArrayList<User> users;

    public UserUI() {
        this.users = new ArrayList<>();
        this.userData = FXCollections.observableArrayList();

        initialize();
        refreshTable();
    }

    private void initialize() {
        view = new BorderPane();
        view.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label("User Management");
        view.setTop(titleLabel);

        // Table
        setupTable();

        // Buttons
        setupButtonPanel();
    }

    // Table View
    private void setupTable() {
        tableView = new TableView<>();

        TableColumn<User, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserId()));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserType()));

        tableView.getColumns().addAll(idCol, nameCol, emailCol, typeCol);
        tableView.setItems(userData);

        view.setCenter(tableView);
    }

    // Button Panel
    private void setupButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10, 0, 0, 0));

        Button addBtn = new Button("Add User");
        Button viewBtn = new Button("View Details");
        Button listBtn = new Button("List All");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> showAddUserDialog());
        viewBtn.setOnAction(e -> {
            User selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showUserDetails(selected);
            } else {
                showAlert("No Selection", "Please select a user.");
            }
        });
        listBtn.setOnAction(e -> refreshTable());
        refreshBtn.setOnAction(e -> refreshTable());

        buttonPanel.getChildren().addAll(addBtn, viewBtn, listBtn, refreshBtn);
        view.setBottom(buttonPanel);
    }

    // Dialog for adding a new user
    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter user information");

        // Initialize Button
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField emailField = new TextField();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Student", "Staff", "Guest");
        typeCombo.setValue("Student");

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                // Check empty
                if (idField.getText().trim().isEmpty() ||
                        nameField.getText().trim().isEmpty() ||
                        emailField.getText().trim().isEmpty()) {
                    showAlert("Error", "All fields required!");
                    return null;
                }

                // Check duplicate
                for (User u : users) {
                    if (u.getUserId().equals(idField.getText().trim())) {
                        showAlert("Error", "ID already exists!");
                        return null;
                    }
                }

                String type = typeCombo.getValue();
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                // Add to Array
                switch (type) {
                    case "Student": return new Student(id, name, email);
                    case "Staff": return new Staff(id, name, email);
                    default: return new Guest(id, name, email);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newUser -> {
            users.add(newUser);
            refreshTable();
            showAlert("Success", "User added!");
        });
    }

    // Shows detailed information for a selected user
    private void showUserDetails(User user) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("User Details");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(new Label(user.getUserId()), 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(new Label(user.getName()), 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(user.getEmail()), 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(new Label(user.getUserType()), 1, 3);
        grid.add(new Label("Booking Limit:"), 0, 4);
        grid.add(new Label(String.valueOf(user.getBookingLimit())), 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setUsers(ArrayList<User> userList) {
        this.users = userList;
        refreshTable();
    }
    private void refreshTable() {
        userData.clear();
        userData.addAll(users);
    }

    public Node getView() {
        return view;
    }

    public void refresh() {
        refreshTable();
    }
}
