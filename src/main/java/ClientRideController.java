import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ClientRideController extends ClientController implements Initializable {

    @FXML
    public Label labelPleaseWait;
    @FXML
    private Button buttonCancel;

    public String username, role, name, gender, email;
    public int age;
    private int driver_id = 0;

    private boolean canceled = false;
    
    public void initialize(URL location, ResourceBundle resources) {
        buttonCancel.setOnAction(this::cancelRide);

        Thread thread = new Thread(() -> {
            System.out.println("Thread Running");
            waitForAccept();
        });
        thread.start();
    }
    
    public void cancelRide(ActionEvent event) {

        canceled = true;
        if (driver_id == 0)
            DBUtils.cancelRideClient(event);
        else
            DBUtils.cancelRide(event, driver_id);
        returnToRequestPage(event);
    }

    private void waitForFinish() {

        System.out.println("checking if driver accepted");
        if (canceled) {
            return;
        }

        if (checkIfDriverFinished()) {
            System.out.println("finished");
            Platform.runLater(() -> DBUtils.changeScene(null, (Stage)buttonCancel.getScene().getWindow(), "client.fxml", "RideShare - Client", username, role, name, age, gender, email));
            return;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("waited a few secs");
        waitForFinish();
    }

    private void waitForAccept() {

        System.out.println("checking if driver accepted");
        if (canceled) {
            return;
        }

        if (checkIfRideAccepted()) {
            System.out.println("accepted");
            waitForFinish();
            return;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("waited a few secs");
        waitForAccept();
    }
    
    private boolean checkIfRideAccepted () {

        Connection connection = null;
        PreparedStatement ps;
        ResultSet rs;

        boolean rideAccepted = false;

        String driverName1 = null;
        String driverGender1 = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7570626", "sql7570626", "xZ7xSsbj28");
            ps = connection.prepareStatement("SELECT corresponding_driver_user_id FROM table_client WHERE user_id = ?");
            ps.setInt(1, DBUtils.getCurrentLoggedInUserID());
            rs = ps.executeQuery();

            if (!rs.isBeforeFirst())
                System.out.println("corresponding_driver_user_id not found");
            else {
                while(rs.next()) {
                    driver_id = rs.getInt("corresponding_driver_user_id");
                }
            }
            System.out.println("accepted by" + driver_id);
            if (driver_id != 0) {

                rideAccepted = true;

                ps = connection.prepareStatement("SELECT name, gender FROM table_user WHERE user_id = ?");
                ps.setInt(1, driver_id);
                rs = ps.executeQuery();


                if (!rs.isBeforeFirst())
                    System.out.println("name, gender not found");
                else {
                    while(rs.next()) {
                        driverName1 = rs.getString("name");
                        driverGender1 = rs.getString("gender");
                    }
                }

                final String driverName = driverName1;
                final String driverGender = driverGender1;
                Platform.runLater(() -> receiveDriver(driverName, driverGender));
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
        return rideAccepted;
    }

    private boolean checkIfDriverFinished () {

        Connection connection = null;
        PreparedStatement ps;
        ResultSet rs;

        boolean rideFinished = false;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7570626", "sql7570626", "xZ7xSsbj28");
            ps = connection.prepareStatement("SELECT corresponding_driver_user_id FROM table_client WHERE user_id = ?");
            ps.setInt(1, DBUtils.getCurrentLoggedInUserID());
            rs = ps.executeQuery();

            if (!rs.isBeforeFirst())
                System.out.println("corresponding_driver_user_id not found");
            else {
                while(rs.next()) {
                    driver_id = rs.getInt("corresponding_driver_user_id");
                }
            }

            if (driver_id == 0) {

                rideFinished = true;

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
        return rideFinished;
    }

    public void receiveDriver(String driverName, String gender) {

        String appendS = "";
        switch (gender) {
            case "Male":
                gender = "he";
                appendS = "s";
                break;
            case "Female":
                gender = "she";
                appendS = "s";
                break;
            case "Other":
                gender = "they";
                break;
            default:
                gender = "db error";
        }
        labelPleaseWait.setText("Your ride was accepted by " + driverName + ".\n" + "Please wait until " + gender + " arrive" + appendS + " at your location.");
    }

    public void finishRide () {

        //labelPleaseWait.setText("Your driver arrived!");
        //buttonCancel.setText("Ok");
        //buttonCancel.setStyle("-fx-background-color: #0060FA;");
        //buttonCancel.setTextFill(new Color(1,1,1,1));
        //buttonCancel.setOnAction(this::returnToRequestPage);
    }

    private void returnToRequestPage (ActionEvent event) {

        DBUtils.changeScene(event, "client.fxml", "RideShare - Client", username, role, name, age, gender, email);
    }

}

