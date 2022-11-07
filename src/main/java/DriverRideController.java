import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
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
import java.sql.*;
import java.util.EventListener;
import java.util.Objects;
import java.util.ResourceBundle;

public class DriverRideController extends DriverController implements Initializable {

    @FXML
    public Label labelWaitsAt;
    @FXML
    private Button buttonCancel, buttonArrived;

    public String username, role, name, gender, email, licensePlate;
    public int age;
    private boolean rideFinishedByDriver = false;
    public void initialize(URL location, ResourceBundle resources) {

        buttonCancel.setOnAction(event -> {
            rideFinishedByDriver = true;
            DBUtils.cancelRide(event, DBUtils.getCurrentLoggedInUserID());
            DBUtils.changeScene(event, "driver.fxml", "RideShare - Driver", username, role, name, age, gender, email, licensePlate);
        });
        buttonArrived.setOnAction(event -> {
            rideFinishedByDriver = true;
            DBUtils.driverArrived(event, DBUtils.getCurrentLoggedInUserID());
            DBUtils.changeScene(event, "driver.fxml", "RideShare - Driver", username, role, name, age, gender, email, licensePlate);
        });

        Thread thread = new Thread(() -> {
            System.out.println("Thread Running");
            waitForCancel();
        });
        thread.start();

    }

    private void waitForCancel() {

        System.out.println("checking if client canceled");
        if (rideFinishedByDriver) {
            return;
        }

        if (checkIfCanceledByClient()) {
            System.out.println("canceled by client");
            Platform.runLater(() -> DBUtils.changeScene(null, "driver.fxml", "RideShare - Driver", username, role, name, age, gender, email, licensePlate));
            return;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("waited a few secs");
        waitForCancel();
    }

    private boolean checkIfCanceledByClient () {

        Connection connection = null;
        PreparedStatement ps;
        ResultSet rs;

        boolean rideCanceledByClient = false;

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://andreilazarov.duckdns.org:3306/RideShare", "root", "chocolate");
            ps = connection.prepareStatement("SELECT ride_started FROM database_driver WHERE user_id = ?");
            ps.setInt(1, DBUtils.getCurrentLoggedInUserID());
            rs = ps.executeQuery();

            if (!rs.isBeforeFirst())
                System.out.println("ride_started not found");
            else {
                while(rs.next())
                    rideCanceledByClient = !rs.getBoolean("ride_started");
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null) {
                try {
                    connection.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rideCanceledByClient;
    }


    public void cancelRide(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING, "This will cancel the current ride. Are you sure you want to proceed?", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Cancel ride - WARNING");
        alert.showAndWait();
    }

}

