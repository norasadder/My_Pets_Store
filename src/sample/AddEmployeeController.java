package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

public class AddEmployeeController {

    @FXML
    private AnchorPane pane;
    @FXML
    private Stage stage;

    @FXML
    private TextField firstName,lastName,phone,city,street,near,email,password,salary;
    @FXML
    private DatePicker birth;
    @FXML
    private JFXRadioButton feMale,male;

    char g;

    @FXML
    public void exitApplication(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    public void minimizeApplication(ActionEvent event)  {
        stage = (Stage) pane.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML
    public void switchToAHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("AHome.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    @FXML
    public void switchTheScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }


    public void save () {
   Unit u = new Unit();
        if(firstName.getText().isEmpty()||lastName.getText().isEmpty()||city.getText().isEmpty()||street.getText().isEmpty()
                ||near.getText().isEmpty()||phone.getText().isEmpty()||birth.getValue()==null||
                ((!male.isSelected())&&(!feMale.isSelected()))||
                email.getText().isEmpty()||password.getText().isEmpty()||salary.getText().isEmpty()){
            u.showAlert("Error","Add Employee","Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
        }
        else {
            try {
                Connection con = u.mySQLConnect();
                Statement st = con.createStatement();
                if (feMale.isSelected()) {
                    g = 'F';
                } else if (male.isSelected()) {
                    g = 'M';
                }

//                String add = "insert into employee values (EID.nextVal,'" +
//                        firstName.getText() + "','" + lastName.getText() + "','" + city.getText() + "','" + street.getText() + "','" + near.getText() + "'," + phone.getText() + ",'" +
//                        birth.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "','" +g+"','"+email.getText()+"','"+password.getText()+"',"+salary.getText() +")" ;

                String add = "insert into employee (FName,LName,City,Street,Near,PNumber,BDate,Gender,Email,UserPassword,Salary) values ('" +
                        firstName.getText() + "','" + lastName.getText() + "','" + city.getText() + "','" + street.getText() + "','" + near.getText() + "'," + phone.getText() + ",'" +
                        birth.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "','" +g+"','"+email.getText()+"','"+password.getText()+"',"+salary.getText() +")" ;

                st.executeUpdate(add) ;
                con.close();

            } catch (SQLException ex) {


            }
        }

    }


}

