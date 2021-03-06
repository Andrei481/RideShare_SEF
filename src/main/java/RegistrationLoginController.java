import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class RegistrationLoginController extends LoginController implements Initializable {
    @FXML
    private Button buttonSignup, buttonLogin;
    @FXML
    private TextField textFieldUsername, textFieldFirstName, textFieldLastName, textFieldAge, textFieldEmail, textFieldLicensePlate;
    @FXML
    private PasswordField fieldPassword, fieldConfirmPassword;
    @FXML
    private ComboBox<String> comboBoxGender;
    @FXML
    private RadioButton radioButtonDriver, radioButtonClient;
    @FXML
    private Label labelLicensePlate, labelError;
    private final ObservableList<String> genders = FXCollections.observableArrayList("Male", "Female", "Other");
    private String firstName, lastName, fullName, email, gender, role, username, password, licensePlate = "";
    private int age;
    private ToggleGroup toggleGroup;
    private boolean ageError = false, emailError = false, passwordError = false, emptyError = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
         * this part makes sure that only one role can be selected
         * if the user selects the driver role => the client role is deselected automatically and vice-versa
         */
        labelLicensePlate.setVisible(false);
        textFieldLicensePlate.setVisible(false);

        toggleGroup = new ToggleGroup();
        radioButtonDriver.setToggleGroup(toggleGroup);
        radioButtonClient.setSelected(true);
        radioButtonClient.setToggleGroup(toggleGroup);

        radioButtonDriver.selectedProperty().addListener((observable, wasPreviouslySelected, isNowSelected) -> {

            if(isNowSelected) {
                labelLicensePlate.setVisible(true);
                textFieldLicensePlate.setVisible(true);
            }
            else {
                labelLicensePlate.setVisible(false);
                textFieldLicensePlate.setVisible(false);
            }

            checkAge(textFieldAge.getText());
            checkEmptyFields();
        });

        textFieldFirstName.textProperty().addListener((observable, oldValue, newValue) -> checkEmptyFields());
        textFieldFirstName.textProperty().addListener((observable, oldValue, newValue) -> checkEmptyFields());
        textFieldAge.textProperty().addListener((observable, oldValue, newValue) -> {

            if(!newValue.matches("\\*d")) {
                textFieldAge.setText(newValue.replaceAll("\\D", ""));
            }
            checkEmptyFields();
            checkAge(textFieldAge.getText());
        });
        comboBoxGender.valueProperty().addListener((observable, oldValue, newValue) -> checkEmptyFields());
        textFieldEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            checkValidEmail(newValue);
            checkEmptyFields();
        });
        textFieldUsername.textProperty().addListener((observable, oldValue, newValue) -> checkEmptyFields());
        fieldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            checkPassword();
            checkEmptyFields();
        });
        fieldConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            checkPassword();
            checkEmptyFields();
        });
        textFieldLicensePlate.textProperty().addListener((observable, oldValue, newValue) -> checkEmptyFields());

        comboBoxGender.setItems(genders);

        buttonSignup.setOnAction(event -> {
            if(!checkEmptyFields())
            {
                firstName = textFieldFirstName.textProperty().getValue();
                lastName = textFieldLastName.textProperty().getValue();
                setName();
                role = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
                age = Integer.parseInt(textFieldAge.textProperty().getValue());
                email = textFieldEmail.textProperty().getValue();
                username = textFieldUsername.textProperty().getValue();
                password = fieldPassword.textProperty().getValue();
                gender = comboBoxGender.getValue();
                if(role.equals("Driver"))
                {
                    licensePlate = textFieldLicensePlate.textProperty().getValue();
                }
                else
                {
                    licensePlate = "";
                }
                System.out.println("Name: "+fullName);
                System.out.println("Age: "+age);
                System.out.println("Gender: "+gender);
                System.out.println("Email: "+email);
                System.out.println("Username: "+username+"\nPassword: "+password+"\nRole: "+role);
                if(age < 18 && role.equals("Driver"))
                {
                    displayError("You must be at least 18 to register as a driver!");
                    System.out.println("Error: Invalid age!");
                }
                else if(!checkValidEmail(email))
                {
                    displayError("The email address you've entered is invalid!");
                    System.out.println("Error: Invalid email address!");
                }
                else
                {
                    //DBUtils.registerUser(event, username, password, role, fullName, age, gender, email);
                    if(role.equals("Client"))
                    {
                        DBUtils.registerClient(event, username, BCrypt.hashpw(password, BCrypt.gensalt()), role, fullName, age, gender, email);
                    }
                    else if(role.equals("Driver"))
                    {
                        DBUtils.registerDriver(event, username, BCrypt.hashpw(password, BCrypt.gensalt()), role, fullName, age, gender, email, licensePlate);
                    }
                }
            }else
            {
                displayError("Please fill all empty fields!");
                System.out.println("Not all fields were completed!");
            }
             // check if username and password contain no whitespace
        });

        buttonLogin.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "RideShare", null, null, null, 0, null, null));
    }

    public boolean checkEmptyFields() {

        emptyError =
                        textFieldFirstName.getText().equals("") ||
                        textFieldLastName.getText().equals("") ||
                        textFieldAge.getText().equals("") ||
                        comboBoxGender.getValue() == null ||
                        textFieldEmail.getText().equals("") ||
                        textFieldUsername.getText().equals("") ||
                        fieldPassword.getText().equals("") ||
                        fieldConfirmPassword.getText().equals("");

       if(radioButtonDriver.isSelected()) {
            if(textFieldLicensePlate.getText().equals("")) {
                emptyError = true;
            }
       }
       updateErrors();
       return emptyError;
    }

    public boolean checkValidEmail(String email)
    {
        boolean result;
        final String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        result = matcher.matches();
        emailError = !result;
        updateErrors();
        return result;
    }
/*
    public void firstNameEntered(ActionEvent event)
    {
        firstName = textFieldFirstName.textProperty().getValue();
        System.out.println("First name: "+firstName);
    }

    public void lastNameEntered(ActionEvent event)
    {
        lastName = textFieldLastName.textProperty().getValue();
        System.out.println("Last name: "+lastName);
    }
*/
    public void setName()
    {
        if(firstName != null && lastName != null)
        {
            fullName = lastName + " " + firstName;
        }
        System.out.println("Full name: "+fullName);
    }

    private void checkAge (String newValue) {

        int currAge = 0;
        if (!newValue.equals("")) {
            try {
                currAge = Integer.parseInt(newValue);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            if (currAge > 999) {
                currAge = currAge / 10;
                textFieldAge.setText(String.valueOf(currAge));
            }
        }
        ageError = (currAge < 18) && (radioButtonDriver.isSelected());
        updateErrors();
    }

    private void checkPassword () {

        passwordError = !fieldPassword.getText().equals(fieldConfirmPassword.getText());
        updateErrors();
    }

    private void updateErrors() {

        boolean anyError = (ageError || emailError || passwordError || emptyError);
        buttonSignup.setDisable(anyError);
        labelError.setVisible(anyError);

        if (anyError) {
            if (emptyError)
                labelError.setText("Please fill all the fields.");
            if (passwordError)
                labelError.setText("Passwords don't match.");
            if (emailError)
                labelError.setText("Please enter a valid email address.");
            if (ageError)
                labelError.setText("You must be at least 18 to register as a driver!");
        }
    }

    /*public void ageEntered(ActionEvent event)
    {
        age = Integer.valueOf(textFieldAge.textProperty().getValue());
        System.out.println("Age: "+age);
    }

    public void emailEntered(ActionEvent event)
    {
        email = textFieldEmail.textProperty().getValue();
        System.out.println("Email address: "+email);
    }

    public void usernameEntered(ActionEvent event)
    {
        username = textFieldUsername.textProperty().getValue();
        System.out.println("Username: "+username);
    }

    public void passwordEntered(ActionEvent event)
    {
        password = fieldPassword.textProperty().getValue();
        System.out.println("Password: "+password);
    }

    public void setGender(ActionEvent event)
    {
        gender = (String)comboBoxGender.getValue();
        System.out.println("Gender: "+gender);
    }

    public void licensePlateEntered()
    {
        licensePlate = textFieldLicensePlate.textProperty().getValue();
        System.out.println("License plate: "+licensePlate);
    }*/
}
