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
import util.Ride;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientController extends LoginController implements Initializable {
    @FXML
    private Label labelWelcome, labelName, labelAge, labelGender, labelEmail, labelUsername, labelUserID, labelRole;
    private final ObservableList<String> locations = FXCollections.observableArrayList("Bd.Vasile Pârvan", "Parcul Rozelor", "Catedrala Mitropolitană", "Iulius Mall", "Calea Aradului", "Calea Șagului");
    @FXML
    private ComboBox<String> comboBoxLocation, comboBoxDestination;
    @FXML
    private Button buttonRequest, buttonLogout;
    @FXML
    private Tab tabClientHistory;
    @FXML
    private ListView<String> listRideHistory;

    private String chosenLocation, chosenDestination;
    private Ride ride;
    private static ObservableList<String> rideList = FXCollections.observableArrayList();
    private boolean emptyLocation = true;
    private boolean emptyDestination = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // REQUEST TAB
        comboBoxLocation.setItems(locations);
        comboBoxDestination.setItems(locations);

        comboBoxLocation.valueProperty().addListener((observable, oldValue, newValue) -> {
            chosenLocation = newValue;
            emptyLocation = chosenLocation.equals("");
            buttonRequest.setDisable(emptyLocation || emptyDestination || Objects.equals(comboBoxLocation.getValue(), comboBoxDestination.getValue()));
            System.out.println("Chosen Location: "+chosenLocation);
        });

        comboBoxDestination.valueProperty().addListener((observable, oldValue, newValue) -> {
            chosenDestination = newValue;
            emptyDestination = chosenDestination.equals("");
            buttonRequest.setDisable(emptyLocation || emptyDestination || Objects.equals(comboBoxLocation.getValue(), comboBoxDestination.getValue()));
            System.out.println("Chosen Destination: "+chosenDestination);
        });

        

        // HISTORY TAB
        tabClientHistory.setOnSelectionChanged(event -> {
            if(tabClientHistory.isSelected())
            {
                DBUtils.checkRideHistory();
                listRideHistory.setItems(DBUtils.getRideHistory());
            }
        });

        // PROFILE TAB
        buttonLogout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "RideShare", null, null, null, 0, null, null));
    }

    public void setUserInfo(String username, String role, String name, int age, String gender, String email)
    {
        // REQUEST TAB
        String[] names = name.split(" ", 2);
        labelWelcome.setText("WELCOME, " + names[1].toUpperCase()+ "!");

        buttonRequest.setOnAction(event -> requestRide (event, username, role, name, age, gender, email));

        // HISTORY TAB


        // PROFILE TAB
        labelName.setText(name);
        labelAge.setText(Integer.toString(age));
        labelGender.setText(gender);
        labelEmail.setText(email);
        labelUsername.setText(username);
        labelUserID.setText('#' + String.valueOf(DBUtils.getCurrentLoggedInUserID()));
        labelRole.setText(role);
    }
    public static ObservableList<String> getRideList()
    {
        return rideList;
    }
    
    public void requestRide (ActionEvent event, String username, String role, String name, int age, String gender, String email) {

        ride = new Ride(DBUtils.getCurrentLoggedInUserID(), chosenLocation, chosenDestination);
        System.out.println("Requesting ride from: "+chosenLocation +" to "+chosenDestination+" for client "+DBUtils.getCurrentLoggedInUserID());
        System.out.println("Ride: " + ride);
        DBUtils.requestRideClient(event, DBUtils.getCurrentLoggedInUserID(), chosenLocation, chosenDestination);
        rideList.add(ride.toString());

        Parent root = null;
        try{
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("clientRide.fxml"));
            root = loader.load();
            ClientRideController clientRideController = loader.getController();
            clientRideController.username = username;
            clientRideController.role = role;
            clientRideController.name = name;
            clientRideController.age = age;
            clientRideController.gender = gender;
            clientRideController.email = email;

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("RideShare - Ride started");
        stage.setScene(new Scene(Objects.requireNonNull(root), stage.getWidth() - 16, stage.getHeight() - 39));
        stage.show();

    }

    public void history()
    {
        System.out.println("Ride History Tab");
    }
}
