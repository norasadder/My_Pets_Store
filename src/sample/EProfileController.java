package sample;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EProfileController implements Initializable {
    private int employeeID ;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField firstName,lastName,phone,city,street,near,email,password,salary;
    @FXML
    private DatePicker birth;

    @FXML
    public void exitApplication(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void minimizeApplication(ActionEvent event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void switchTheScene(ActionEvent event) throws IOException {
        String name =LogOutController.flag+((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml";
        if (name.contains("Logout")){
            name = "Logout.fxml";
        }
        Parent root = FXMLLoader.load(getClass().getResource(name));        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
    @FXML
    public void save(ActionEvent event) throws IOException {
        firstName.setEditable(false);
        lastName.setEditable(false);
        phone.setEditable(false);
        birth.setEditable(false);
        city.setEditable(false);
        street.setEditable(false);
        near.setEditable(false);
        email.setEditable(false);
        password.setEditable(false);
        LocalDate date = birth.getValue();
        String finalDate = "TO_DATE('" + date + "','YYYY-MM-DD')";
        Unit u = new Unit();
//        Connection connect = u.connectOracle();
         Connection connect = u.mySQLConnect();
        if(firstName.getText().isEmpty()||lastName.getText().isEmpty()||city.getText().isEmpty()||street.getText().isEmpty()
                ||near.getText().isEmpty()||phone.getText().isEmpty()||birth.getValue()==null||
                email.getText().isEmpty()||password.getText().isEmpty()){
            u.showAlert("Error","Update Profile Information","Please enter all Information field and try again!", Alert.AlertType.ERROR);
        }
        else {
            try {
                String saveStm = "update Employee set FName = '" + firstName.getText()+"'"+"," + "LName = '"+lastName.getText()+"'"+","+ "City = '"+city.getText()+"'"+","+"Street = '"+street.getText()+"'"+","+
                        "Near = '" +near.getText()+"'"+","+ "PNumber = '" +Integer.parseInt(phone.getText())+"'"+","+"BDate = '"+birth.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"'"+","+"Email = '"+ email.getText() +"'"+","+ "UserPassword = '" + password.getText()+"'"
                        +"where UserId ="+String.valueOf(employeeID) ;
//                String saveStm = "update Employee set FName = '" + firstName.getText()+"'"+"," + "LName = '"+lastName.getText()+"'"+","+ "City = '"+city.getText()+"'"+","+"Street = '"+street.getText()+"'"+","+
//                        "Near = '" +near.getText()+"'"+","+ "PNumber = '" +Integer.parseInt(phone.getText())+"'"+","+"BDate = "+finalDate+","+"Email = '"+ email.getText() +"'"+","+ "UserPassword = '" + password.getText()+"'"
//                        +"where UserId ="+String.valueOf(employeeID) ;
                Statement statement = connect.createStatement();
                statement.executeUpdate(saveStm) ;
                connect.commit();
            }
            catch (SQLException ex) {


            } }


    }
    @FXML
    public void edit(ActionEvent event) throws IOException {
        if (((Control)event.getSource()).getId().equals("nameBtn")){
            firstName.setEditable(true);
            lastName.setEditable(true);
        }
        else if (((Control)event.getSource()).getId().equals("phoneBtn"))
            phone.setEditable(true);
        else if (((Control)event.getSource()).getId().equals("birthBtn"))
            birth.setEditable(true);
        else if (((Control)event.getSource()).getId().equals("addressBtn")) {
            city.setEditable(true);
            street.setEditable(true);
            near.setEditable(true);
        }
        else if (((Control)event.getSource()).getId().equals("emailBtn"))
            email.setEditable(true);
        else if (((Control)event.getSource()).getId().equals("passwordBtn"))
            password.setEditable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employeeID = LogOutController.userID ;
        Unit u = new Unit();
        LocalDate date;

        try {
//            Connection con = u.connectOracle();
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
            String searchStr = "select FName , LName ,City , Street , Near , PNumber ,BDate, Email , userPassword , salary From Employee where UserID =" + employeeID;
            ResultSet rs = statement.executeQuery(searchStr) ;
            while (rs.next()) {
                firstName.setText(rs.getString(1));
                lastName.setText(rs.getString(2));
                city.setText(rs.getString(3));
                street.setText(rs.getString(4));
                near.setText(rs.getString(5));
                phone.setText(rs.getString(6));
                System.out.println(rs.getString(7));
                date = LocalDate.parse(rs.getString(7)) ;
                birth.setValue(date);
                email.setText(rs.getString(8));
                password.setText(rs.getString(9));
                salary.setText(rs.getString(10));
            }
            con.close();
        } catch (SQLException throwables) {

        }

    }
    }

