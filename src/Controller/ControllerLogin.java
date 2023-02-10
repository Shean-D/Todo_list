package Controller;

import DB.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ControllerLogin {
    public AnchorPane root;
    public PasswordField txtPassword;
    public TextField txtUserName;

    public static String loginUserName;
    public static String loginUserID;

    public void initialize (){

        txtUserName.requestFocus();
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/view/Registration.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registration");
        primaryStage.centerOnScreen();

    }

    public void PasswordOnAction(ActionEvent actionEvent) {
        login();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void login(){
        String username = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name =? and password =?");
            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){

                loginUserName = resultSet.getString(2);
                loginUserID = resultSet.getString(1);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../View/ToDoForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.centerOnScreen();
                primaryStage.setTitle("To Do Form");

            }else {
                new Alert(Alert.AlertType.ERROR,"User name or password does not matched").showAndWait();
                txtUserName.clear();
                txtPassword.clear();
                txtUserName.requestFocus();
            }
        }
        catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }
}
