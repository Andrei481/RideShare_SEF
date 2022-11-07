import javafx.scene.control.Alert;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DBUtilsTest {

    @Test
    void registerClient() {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement st = null;
        ResultSet resultSet = null;
        ResultSet rs = null;

        String username = "user123";
        String password = "password123";
        String role = "Client";
        String name = "John Smith";
        int age = 46;
        String gender = "Male";
        String email = "johnsmith@gmail.com";
        ActionEvent event = null;

        PreparedStatement psCheckUserAlreadyExists = null;
        try{
            connection = DriverManager.getConnection("jdbc:mariadb://andreilazarov.duckdns.org:3306/RideShare", "root", "chocolate");
            psCheckUserAlreadyExists = connection.prepareStatement("SELECT * FROM database_user WHERE username = ?");
            psCheckUserAlreadyExists.setString(1, username);
            resultSet = psCheckUserAlreadyExists.executeQuery();

            if(resultSet.isBeforeFirst())
            {
                fail("User already exists!");
            }
            else
            {
                // register into the user table
                psInsert = connection.prepareStatement("INSERT INTO database_user (username, password, role, name, age, gender, email) VALUES (?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, role);
                psInsert.setString(4, name);
                psInsert.setInt(5, age);
                psInsert.setString(6, gender);
                psInsert.setString(7, email);
                psInsert.executeUpdate();

                st = connection.prepareStatement("SELECT MAX(user_id) from database_user");
                rs = st.executeQuery();
                rs.next();
                int uid = rs.getInt(1);
                if(uid <= 0)
                {
                    fail("user doesn't exist");
                }
                DBUtils.insertIntoClientDatabase(event, uid, false, null, null, 0);
                System.out.println("Client registered successfully!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            /*
              close db connections to avoid memory leaks
              */
            if(resultSet != null)
            {
                try{
                    resultSet.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(psCheckUserAlreadyExists != null)
            {
                try{
                    psCheckUserAlreadyExists.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(psInsert != null)
            {
                try {
                    psInsert.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(connection != null)
            {
                try{
                    connection.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void registerDriver() {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement st = null;
        ResultSet resultSet = null;
        ResultSet rs = null;
        ActionEvent event = null;

        String username = "driver123";
        String password = "password123";
        String role = "Driver";
        String name = "Laura Brown";
        int age = 28;
        String gender = "Female";
        String email = "laurabrown@gmail.com";
        String licensePlate = "TM22BRN";

        PreparedStatement psCheckUserAlreadyExists = null;
        try{
            connection = DriverManager.getConnection("jdbc:mariadb://andreilazarov.duckdns.org:3306/RideShare", "root", "chocolate");
            psCheckUserAlreadyExists = connection.prepareStatement("SELECT * FROM database_user WHERE username = ?");
            psCheckUserAlreadyExists.setString(1, username);
            resultSet = psCheckUserAlreadyExists.executeQuery();

            if(resultSet.isBeforeFirst())
            {
                fail("User already exists!");
            }
            else
            {
                // register into the user table
                psInsert = connection.prepareStatement("INSERT INTO database_user (username, password, role, name, age, gender, email) VALUES (?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, role);
                psInsert.setString(4, name);
                psInsert.setInt(5, age);
                psInsert.setString(6, gender);
                psInsert.setString(7, email);
                psInsert.executeUpdate();

                st = connection.prepareStatement("SELECT MAX(user_id) from database_user");
                rs = st.executeQuery();
                rs.next();
                int uid = rs.getInt(1);
                if(uid <= 0)
                {
                    fail("user doesn't exist");
                }
                DBUtils.insertIntoDriverDatabase(event, uid, licensePlate, false, 0);
                System.out.println("Driver registered successfully!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            /*
              close db connections to avoid memory leaks
              */
            if(rs != null)
            {
                try {
                    rs.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(resultSet != null)
            {
                try{
                    resultSet.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(psCheckUserAlreadyExists != null)
            {
                try{
                    psCheckUserAlreadyExists.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(psInsert != null)
            {
                try {
                    psInsert.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(connection != null)
            {
                try{
                    connection.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void invalidLoginUser() {
        String username = "IDONTEXIST";
        String password = "1234";
        ActionEvent event = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mariadb://andreilazarov.duckdns.org:3306/RideShare", "root", "chocolate");
            preparedStatement = connection.prepareStatement("SELECT user_id, password, role, name, age, gender, email FROM database_user WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            /*
              if this is false => username not found in the db => notify user
              */

            if(!resultSet.isBeforeFirst())
            {
                fail("user not found");
            }
            /*
             *  if the user is found in the DB => compare the password they wrote with the one originally in the db
             */
            else {
                DBUtils.loginUser(event, username, password);
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }finally {
            /*
             * close all connections to the db
             */

            if(resultSet != null)
            {
                try{
                    resultSet.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null)
            {
                try{
                    preparedStatement.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(connection != null)
            {
                try {
                    connection.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void validLoginUser() {
        String username = "user123";
        String password = "parola";
        ActionEvent event = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mariadb://andreilazarov.duckdns.org:3306/RideShare", "root", "chocolate");
            preparedStatement = connection.prepareStatement("SELECT user_id, password, role, name, age, gender, email FROM database_user WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            /*
              if this is false => username not found in the db => notify user
              */

            if(!resultSet.isBeforeFirst())
            {
                fail("user not found");
            }
            /*
             *  if the user is found in the DB => compare the password they wrote with the one originally in the db
             */
            else {
                System.out.println("Logging in user");
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }finally {
            /*
             * close all connections to the db
             */

            if(resultSet != null)
            {
                try{
                    resultSet.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null)
            {
                try{
                    preparedStatement.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if(connection != null)
            {
                try {
                    connection.close();
                }catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}