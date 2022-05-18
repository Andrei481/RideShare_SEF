import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);

    private double movementVariable = 2;
    private int destinationX = 300, destinationY = 300;

    @FXML
    private Rectangle shape1;

    @FXML
    private AnchorPane scene;

    @FXML
    private Button buttonStartRide;

    @FXML
    void start(ActionEvent event) {
        shape1.setVisible(true);
        buttonStartRide.setOpacity(0);
        shape1.setLayoutY(200);
        shape1.setLayoutX(280);
    }

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {
            if(wPressed.get()) {
                shape1.setLayoutY(shape1.getLayoutY() - movementVariable);
            }

            if(sPressed.get()){
                shape1.setLayoutY(shape1.getLayoutY() + movementVariable);
            }

            if(aPressed.get()){
                shape1.setLayoutX(shape1.getLayoutX() - movementVariable);
            }

            if(dPressed.get()){
                shape1.setLayoutX(shape1.getLayoutX() + movementVariable);
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        movementSetup();
        shape1.setVisible(false);

        keyPressed.addListener(((observableValue, aBoolean, t1) -> {
            if(!aBoolean){
                timer.start();
            } else {
                timer.stop();
            }
        }));
    }

    public void movementSetup(){
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W) {
                wPressed.set(true);
            }

            if(e.getCode() == KeyCode.A) {
                aPressed.set(true);
            }

            if(e.getCode() == KeyCode.S) {
                sPressed.set(true);
            }

            if(e.getCode() == KeyCode.D) {
                dPressed.set(true);
            }
        });

        scene.setOnKeyReleased(e ->{
            if(e.getCode() == KeyCode.W) {
                wPressed.set(false);
            }

            if(e.getCode() == KeyCode.A) {
                aPressed.set(false);
            }

            if(e.getCode() == KeyCode.S) {
                sPressed.set(false);
            }

            if(e.getCode() == KeyCode.D) {
                dPressed.set(false);
            }
        });
    }

    public boolean checkReachedDestination()
    {
        if((shape1.getLayoutX() == destinationX) && (shape1.getLayoutY() == destinationY))
        {
            return true;
        }
        return false;
    }
}