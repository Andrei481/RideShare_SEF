import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DriverController extends LoginController implements Initializable {
    @FXML
    public Label labelName, labelAge, labelGender, labelEmail, labelUsername, labelUserID, labelRole, labelPlate, labelSelectedRide;
    @FXML
    private Button buttonStart, buttonLogout, buttonRefresh;
    @FXML
    private ListView<String> listRides;
    @FXML
    private ListView<String> listRideHistory;
    @FXML
    private Tab tabRideHistory;

    private ObservableList<String> rides = FXCollections.observableArrayList();
    private int client_id;
    private String selectedRide;
    private String location;
    private String clientName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // RIDES TAB
        refreshRides();
        listRides.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> {
                    selectedRide = new_val;
                    if (selectedRide != null) {
                        labelSelectedRide.setText("Selected ride:" + '\n' + selectedRide);
                        clientName = new_val.split(", ")[1];
                        client_id = DBUtils.getIDfromName(clientName);
                        System.out.println("client id: |"+client_id+"|");
                        buttonStart.setDisable(false);
                    }
                    else {
                        labelSelectedRide.setText("Please select a ride");
                        buttonStart.setDisable(true);
                    }
                });
        buttonRefresh.setOnAction(event -> refreshRides());

        // HISTORY TAB
        tabRideHistory.setOnSelectionChanged(event -> {
            if(tabRideHistory.isSelected())
            {
                DBUtils.checkRideHistory();
                listRideHistory.setItems(DBUtils.getRideHistory());
            }
        });
        // PROFILE TAB
        buttonLogout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "RideShare"));
    }

    public void setUserInfo(String username, String role, String name, int age, String gender, String email, String licensePlate)
    {
        // RIDES TAB
        buttonStart.setOnAction(event -> startRide(event, username, role, name, age, gender, email, licensePlate));

        // HISTORY TAB

        // PROFILE TAB
        labelName.setText(name);
        labelAge.setText(Integer.toString(age));
        labelGender.setText(gender);
        labelEmail.setText(email);
        labelUsername.setText(username);
        labelUserID.setText('#' + String.valueOf(DBUtils.getCurrentLoggedInUserID()));
        labelRole.setText(role);
        labelPlate.setText(licensePlate);

    }

    public void startRide(ActionEvent event, String username, String role, String name, int age, String gender, String email, String licensePlate) {

        String lastSelectedRide = selectedRide;
        refreshRides();
        if (!listRides.getItems().contains(lastSelectedRide)) {
            displayError("Selected ride was canceled!");
            return;
        }

        Parent root = null;
        try{
            DBUtils.acceptRideDriver(event, DBUtils.getCurrentLoggedInUserID(), client_id);
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("driverRide.fxml"));
            root = loader.load();
            DriverRideController driverRideController = loader.getController();
            //driverRideController.labelWaitsAt.setText("Your client " + clientName +" waits at " + lastSelectedRide.split(" ")[0].split(",")[0] + ".");
            driverRideController.labelWaitsAt.setText("Your client " + clientName +" waits at " + lastSelectedRide.split("⟶")[0]);
            driverRideController.username = username;
            driverRideController.role = role;
            driverRideController.name = name;
            driverRideController.age = age;
            driverRideController.gender = gender;
            driverRideController.email = email;
            driverRideController.licensePlate = licensePlate;

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("RideShare - Ride started");
        stage.setScene(new Scene(Objects.requireNonNull(root), stage.getWidth() - 0, stage.getHeight() - 37));
        stage.show();

    }

    private void refreshRides () {
        DBUtils.checkAvailableRides();
        listRides.setItems(DBUtils.getAvailableRidesList());

        buttonRefresh.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> buttonRefresh.setDisable(false));

        });
        thread.start();

    }
}
