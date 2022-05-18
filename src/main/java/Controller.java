import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button buttonLogin, buttonSeeMap;
    @FXML
    private Button buttonSignup;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordField;

    private static Stage window;
    private static Scene prev;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.loginUser(event, textFieldUsername.getText(), passwordField.getText());
            }
        });

        buttonSignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "register.fxml", "Signup", null, null, null, 0, null, null, null);
            }
        });
    }

    public void displayError(String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Oops!");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void changePage(String fxml){
        try {
            Parent root = FXMLLoader.load(Controller.class.getResource(fxml));
            prev = window.getScene();
            Scene scene = new Scene(root, 1300, 800);
            window.setScene(scene);
            window.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void goToMap(ActionEvent event)
    {
        DBUtils.changeScene(event, "map.fxml", "Map", null, null, null, 0, null, null, null);
    }
}
