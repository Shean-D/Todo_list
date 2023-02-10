package Controller;

import DB.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ControllerRegistration {
    public TextField txtNewPassword;
    public TextField txtConfirmPassword;
    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public Button btnRegister;
    public TextField txtEmail;
    public TextField txtUserName;
    public Label lblUserID;
    public AnchorPane root;
    public Button btnBackToLogin;

    public void initialize() {
        setvisibility(false);
        setdisable(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        registration();

    }

    public void setvisibility(boolean visibility) {
        lblPasswordNotMatched1.setVisible(visibility);
        lblPasswordNotMatched2.setVisible(visibility);
    }

    public void boardercolors(String color) {
        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setdisable(false);
        txtUserName.requestFocus();
        autoIncrementID();
       // Connection connection = DBConnection.getInstance().getConnection();
        //System.out.println(connection);
    }

    public void setdisable(boolean disable) {
        txtUserName.setDisable(disable);
        txtEmail.setDisable(disable);
        txtNewPassword.setDisable(disable);
        txtConfirmPassword.setDisable(disable);
        btnRegister.setDisable(disable);
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        registration();
    }

    public void registration() {

        if (txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        }else if (txtEmail.getText().trim().isEmpty()){
            txtEmail.requestFocus();
        }else if (txtNewPassword.getText().trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }else {

            String newpassword = txtNewPassword.getText();
            String confirmpassword = txtConfirmPassword.getText();

            if (newpassword.equals(confirmpassword)) {
                boardercolors("transparent");
                setvisibility(false);

                String id = lblUserID.getText();
                String username = txtUserName.getText();
                String email = txtEmail.getText();

                Connection connection = DBConnection.getInstance().getConnection();

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
                    preparedStatement.setObject(1, id);
                    preparedStatement.setObject(2, username);
                    preparedStatement.setObject(3, email);
                    preparedStatement.setObject(4, confirmpassword);

                    int i = preparedStatement.executeUpdate();

                    if (i > 0) {
                        Parent parent = FXMLLoader.load(this.getClass().getResource("/view/Login.fxml"));
                        Scene scene = new Scene(parent);

                        Stage primaryStage = (Stage) root.getScene().getWindow();
                        primaryStage.setScene(scene);
                        primaryStage.setTitle("Login");
                        primaryStage.centerOnScreen();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Something went wrong").showAndWait();
                    }

                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }


            } else {
                boardercolors("red");
                boardercolors("red");
                setvisibility(true);

                txtNewPassword.clear();
                txtConfirmPassword.clear();
                txtNewPassword.requestFocus();
            }
        }
    }
    public void autoIncrementID (){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");


            if (resultSet.next()){
                String lastID = resultSet.getString(1);
                lastID = lastID.substring(1, lastID.length());

                int intID = Integer.parseInt(lastID);
                intID++;
                System.out.println(lastID);
                if (intID < 10){
                    lblUserID.setText("U00"+ intID);
                }else if (intID < 100){
                    lblUserID.setText("U0"+intID);
                }else{
                    lblUserID.setText("U"+intID);
                }

            }else {
                lblUserID.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void btnBackToLoginOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/view/Login.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
        primaryStage.setScene(scene);
    }
}