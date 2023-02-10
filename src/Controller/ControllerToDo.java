package Controller;

import DB.DBConnection;
import TM.TodoTM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.print.Paper;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ControllerToDo {
    public Label lblTitle;
    public Label lblUserID;
    public AnchorPane root;
    public AnchorPane subroot;
    public TextField txtDescription;
    public ListView<TodoTM> lstToDo;
    public TextField txtSelectedTodo;
    public Button btnUpdate;
    public Button btnDelete;


    public void initialize() {
        lblTitle.setText("Hi " + ControllerLogin.loginUserName + "...! Welcome to ToDo List..");
        lblUserID.setText(ControllerLogin.loginUserID);
        subroot.setVisible(false);
        loadList();
        setDisable(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoTM>() {
            @Override
            public void changed(ObservableValue<? extends TodoTM> observableValue, TodoTM todoTM, TodoTM t1) {
               setDisable(false);
               subroot.setVisible(false);
                txtSelectedTodo.requestFocus();

                TodoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if (selectedItem == null){
                    return;
                }
                txtSelectedTodo.setText(selectedItem.getDescription());


            }
        });
    }
    public void loadList(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1,ControllerLogin.loginUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<TodoTM> todo = lstToDo.getItems();

            todo.clear();
            while (resultSet.next()){
                todo.add(new TodoTM(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3)));
            }
            lstToDo.refresh();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnLogout(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Logout.?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            Parent parent = FXMLLoader.load(this.getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddNewTodoOnAction(ActionEvent actionEvent) {
        lstToDo.getSelectionModel().clearSelection();
        setDisable(true);
        subroot.setVisible(true);
        txtDescription.requestFocus();

    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String description = txtDescription.getText();
        String id = autoIncrementID();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,lblUserID.getText());

            preparedStatement.executeUpdate();
            txtDescription.clear();
            subroot.setVisible(false);
            loadList();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String autoIncrementID() {
        Connection connection = DBConnection.getInstance().getConnection();
        String id = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");


            if (resultSet.next()) {
                String lastID = resultSet.getString(1);
                lastID = lastID.substring(1, lastID.length());

                int intID = Integer.parseInt(lastID);
                intID++;

                if (intID < 10) {
                    id = "T00"+intID;
                } else if (intID < 100) {
                    id = "T0"+intID;
                } else {
                    id = "T"+intID;
                }

            } else {
                id = "T001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;

    }
    public void setDisable (boolean isdisable){
        txtSelectedTodo.setDisable(isdisable);
        btnDelete.setDisable(isdisable);
        btnUpdate.setDisable(isdisable);

        txtSelectedTodo.clear();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this ToDO.?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            String id = lstToDo.getSelectionModel().getSelectedItem().getId();
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadList();
                setDisable(true);

            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String text = txtSelectedTodo.getText();
        String id = lstToDo.getSelectionModel().getSelectedItem().getId();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,text);
            preparedStatement.setObject(2,id);

            int i = preparedStatement.executeUpdate();
            loadList();
            setDisable(true);
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}