package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.util.DataManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Collections;
import java.util.Optional;

public class MainController {

    @FXML
    private TextField roomNoField, priceField, nameField, phoneField;
    @FXML
    private ComboBox<String> roomTypeBox;
    @FXML
    private ComboBox<Room> bookRoomSelectBox;
    @FXML
    private Spinner<Integer> nightsSpinner;

    @FXML
    private TableView<Room> roomTable;
    @FXML
    private TableColumn<Room, Integer> colRoomNo;
    @FXML
    private TableColumn<Room, String> colRoomType;
    @FXML
    private TableColumn<Room, Double> colRoomPrice;
    @FXML
    private TableColumn<Room, String> colRoomStatus;

    @FXML
    private TableView<Booking> activeBookingsTable;
    @FXML
    private TableColumn<Booking, String> colBookCustomer;
    @FXML
    private TableColumn<Booking, Integer> colBookRoom;
    @FXML
    private TableColumn<Booking, Integer> colBookNights;
    @FXML
    private TableColumn<Booking, Double> colBookTotal;

    @FXML
    private TableView<Booking> checkoutTable;
    @FXML
    private TableColumn<Booking, String> colCheckCustomer;
    @FXML
    private TableColumn<Booking, Integer> colCheckRoom;
    @FXML
    private TableColumn<Booking, Integer> colCheckNights;
    @FXML
    private TableColumn<Booking, Double> colCheckTotal;
    @FXML
    private TableColumn<Booking, Void> colCheckAction;

    @FXML
    private TableView<Booking> historyTable;
    @FXML
    private TableColumn<Booking, String> colHistCustomer;
    @FXML
    private TableColumn<Booking, Integer> colHistRoom;
    @FXML
    private TableColumn<Booking, Double> colHistTotal;

    private ObservableList<Room> rooms;
    private ObservableList<Booking> activeBookings;
    private ObservableList<Booking> checkoutHistory;

    private final String ROOM_FILE = "rooms.dat";
    private final String ACTIVE_FILE = "active.dat";
    private final String HISTORY_FILE = "history.dat";

    @FXML
    public void initialize() {
        rooms = FXCollections.observableArrayList(DataManager.<Room>loadList(ROOM_FILE));
        activeBookings = FXCollections.observableArrayList(DataManager.<Booking>loadList(ACTIVE_FILE));
        checkoutHistory = FXCollections.observableArrayList(DataManager.<Booking>loadList(HISTORY_FILE));

        roomTypeBox.setItems(FXCollections.observableArrayList("Standard", "Deluxe", "Suite"));
        nightsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 1));

        setupRoomComboBox();
        setupTables();
        refreshData();
    }

    private void setupRoomComboBox() {
        bookRoomSelectBox.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room r) {
                return r == null ? null : r.getDisplayString();
            }

            @Override
            public Room fromString(String s) {
                return null;
            }
        });
    }

    private void setupTables() {
        colRoomNo.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colRoomPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colRoomStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        roomTable.setItems(rooms);

        colBookCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colBookRoom.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        colBookNights.setCellValueFactory(new PropertyValueFactory<>("nights"));
        colBookTotal.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        activeBookingsTable.setItems(activeBookings);

        colCheckCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCheckRoom.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        colCheckNights.setCellValueFactory(new PropertyValueFactory<>("nights"));
        colCheckTotal.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        checkoutTable.setItems(activeBookings);
        addCheckoutButtonToTable();

        colHistCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colHistRoom.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        colHistTotal.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        historyTable.setItems(checkoutHistory);
    }

    private void refreshData() {
        // Week 8: Collections sorting
        Collections.sort(rooms);

        ObservableList<Room> availableRooms = FXCollections.observableArrayList();
        for (Room r : rooms) {
            if (r.getStatus().equals("Available"))
                availableRooms.add(r);
        }
        bookRoomSelectBox.setItems(availableRooms);

        roomTable.refresh();
        activeBookingsTable.refresh();
        checkoutTable.refresh();
        historyTable.refresh();
    }

    @FXML
    private void addRoom() {
        try {
            int roomNo = Integer.parseInt(roomNoField.getText());
            String type = roomTypeBox.getValue();
            double price = Double.parseDouble(priceField.getText());

            if (type == null) {
                showAlert(Alert.AlertType.WARNING, "Select a Room Type.");
                return;
            }
            for (Room r : rooms) {
                if (r.getRoomNo() == roomNo) {
                    showAlert(Alert.AlertType.ERROR, "Room already exists!");
                    return;
                }
            }

            rooms.add(new Room(roomNo, type, price, "Available"));
            DataManager.saveList(rooms, ROOM_FILE);

            roomNoField.clear();
            priceField.clear();
            roomTypeBox.getSelectionModel().clearSelection();
            refreshData();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input. Use numbers for Room No and Price.");
        }
    }

    @FXML
    private void bookRoom() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        Room selectedRoom = bookRoomSelectBox.getValue();
        int nights = nightsSpinner.getValue();

        if (name.isEmpty() || phone.isEmpty() || selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all booking details.");
            return;
        }

        double total = selectedRoom.getPrice() * nights;
        Customer c = new Customer(name, phone);
        activeBookings.add(new Booking(c, selectedRoom.getRoomNo(), nights, total));
        selectedRoom.setStatus("Occupied");

        DataManager.saveList(rooms, ROOM_FILE);
        DataManager.saveList(activeBookings, ACTIVE_FILE);

        nameField.clear();
        phoneField.clear();
        bookRoomSelectBox.getSelectionModel().clearSelection();
        refreshData();
    }

    private void addCheckoutButtonToTable() {
        Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("[→] Checkout");
            {
                btn.getStyleClass().add("table-btn");
                btn.setOnAction(event -> {
                    Booking data = getTableView().getItems().get(getIndex());
                    processCheckout(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        colCheckAction.setCellFactory(cellFactory);
    }

    private void processCheckout(Booking b) {
        Room bookedRoom = rooms.stream().filter(r -> r.getRoomNo() == b.getRoomNo()).findFirst().orElse(null);

        if (showBillingPopup(b, bookedRoom)) {
            activeBookings.remove(b);
            checkoutHistory.add(b);

            DataManager.saveList(activeBookings, ACTIVE_FILE);
            DataManager.saveList(checkoutHistory, HISTORY_FILE);

            if (bookedRoom != null) {
                bookedRoom.setStatus("Cleaning...");
                DataManager.saveList(rooms, ROOM_FILE);
                refreshData();

                // Week 3 & 4: Multithreading & Synchronization
                new Thread(() -> {
                    synchronized (bookedRoom) {
                        try {
                            Thread.sleep(2500); // Simulate room cleaning time
                            bookedRoom.setStatus("Available");
                            DataManager.saveList(rooms, ROOM_FILE);
                            Platform.runLater(this::refreshData); // Safely update UI thread
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                showAlert(Alert.AlertType.INFORMATION, "Checkout complete. Room is being cleaned.");
            }
        }
    }

    private boolean showBillingPopup(Booking b, Room r) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Billing Summary");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("custom-dialog");

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 50, 20, 20));

        double tax = b.getTotalBill() * 0.05;
        double grandTotal = b.getTotalBill() + tax;

        grid.add(new Label("Customer:"), 0, 0);
        grid.add(new Label(b.getCustomerName()), 1, 0);
        grid.add(new Label("Room:"), 0, 1);
        grid.add(new Label("#" + b.getRoomNo() + " (" + (r != null ? r.getType() : "N/A") + ")"), 1, 1);
        grid.add(new Label("Nights:"), 0, 2);
        grid.add(new Label(String.valueOf(b.getNights())), 1, 2);
        grid.add(new Separator(), 0, 3, 2, 1);
        grid.add(new Label("Room Charge:"), 0, 4);
        grid.add(new Label("₹" + b.getTotalBill()), 1, 4);
        grid.add(new Label("Tax (5%):"), 0, 5);
        grid.add(new Label("₹" + String.format("%.2f", tax)), 1, 5);
        grid.add(new Separator(), 0, 6, 2, 1);

        Label grandLbl = new Label("Grand Total");
        grandLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #deb887;");
        Label valLbl = new Label("₹" + String.format("%.2f", grandTotal));
        valLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #deb887;");
        grid.add(grandLbl, 0, 7);
        grid.add(valLbl, 1, 7);

        dialog.getDialogPane().setContent(grid);
        ButtonType confirmType = new ButtonType("Confirm Checkout", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, confirmType);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == confirmType;
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        alert.show();
    }
}
