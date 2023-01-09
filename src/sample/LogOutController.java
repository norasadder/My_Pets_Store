package sample;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class LogOutController {
    public static int userID;
    public static String flag;
    public static int cartID;
    @FXML
    private AnchorPane pane;
    @FXML
    private AnchorPane account;
    @FXML
    private JFXToggleButton signUp;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField fName,lName,city,street,near,pNumber,email,password,confirmPassword,emailLogIn,passwordLogIn;
    @FXML
    private JFXRadioButton feMale,male;

    @FXML
    public void exitApplication(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    public void minimizeApplication(ActionEvent event)  {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML
    public void switchTheScene(ActionEvent event) throws IOException {
        Unit u = new Unit();
        String idStr;
        String searchStr;
        try {
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            if(emailLogIn.getText().equals("admin@petsstore")) {
                System.out.println(passwordLogIn.getText());
                if (passwordLogIn.getText().equals("admin@123")) {
                    flag = "A" ;
                    Parent root = FXMLLoader.load(getClass().getResource("AHome.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.setFill(Color.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                    stage.setResizable(false);
                }
                else {
                    u.showAlert("Error","Log In","Incorrect Password!", Alert.AlertType.ERROR);
                }
            }
            else {
            if (emailLogIn.getText().contains("@petsemployee")) {
                idStr = "select UserID,userpassword from Employee where Email = '" + emailLogIn.getText() + "'";
                flag = "E";
                searchStr = "select count(Email) from Employee where email ='"+emailLogIn.getText()+"'";
            } else {
                idStr = "select UserID,userpassword from Customer where Email = '" + emailLogIn.getText() + "'";
                flag = "";
                searchStr = "select count(Email) from Customer where email ='"+emailLogIn.getText()+"'";
            }
            ResultSet check = s.executeQuery(searchStr);
            check.next();
            if (check.getInt(1)==0)
                u.showAlert("Error","Log In","Email not found,Create an account first!", Alert.AlertType.ERROR);
            else {
                ResultSet rs = s.executeQuery(idStr);
                rs.next();
                if (!rs.getString("userpassword").equals(passwordLogIn.getText()))
                    u.showAlert("Error","Log In","Password and email does not match,Check your passowrd!", Alert.AlertType.ERROR);
                else {
                    userID = rs.getInt("UserId");
                    updateCartId();
                }
                    Parent root = FXMLLoader.load(getClass().getResource(flag+"Home.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.setFill(Color.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                    stage.setResizable(false);

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    @FXML
    public void createAnAccount(ActionEvent e){

        Unit u = new Unit();
        LocalDate date = datePicker.getValue();
        if(fName.getText().isEmpty()||lName.getText().isEmpty()||city.getText().isEmpty()||street.getText().isEmpty()
                ||near.getText().isEmpty()||pNumber.getText().isEmpty()||datePicker.getValue()==null||
                ((!male.isSelected())&&(!feMale.isSelected()))||
                email.getText().isEmpty()||password.getText().isEmpty()||confirmPassword.getText().isEmpty()){
            u.showAlert("Error","Create an account","Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
        }

        else if( (!password.getText().equals(confirmPassword.getText())) )
            u.showAlert("Error","Create an account","Passwords do not match,try again!", Alert.AlertType.ERROR);
        else {
            try {
                int pNum = Integer.parseInt(pNumber.getText());
                String finalDate = "TO_DATE('" + date + "','YYYY-MM-DD')";
                String gender;
                if (male.isSelected())
                    gender = "M";
                else gender = "F";
                try {
                    Connection con = u.mySQLConnect();
                    con.setAutoCommit(false);
                    Statement statement = con.createStatement();
                    String searchStr = "select count(Email) from Customer where email ='"+email.getText()+"'";
                    ResultSet rs = statement.executeQuery(searchStr);
                    rs.next();
                    if (rs.getInt(1)!=0)
                        u.showAlert("Error","Create an account","User exists,try again!", Alert.AlertType.ERROR);
                    else{
                        String insertStr = "insert into Customer (FName,LName,City,Street,Near,PNumber,BDate,Gender,Email,userPassword) values(" +"'"+fName.getText() + "'," + "'"+lName.getText() + "'," + "'"+city.getText()
                                + "'," + "'"+street.getText() + "'," + "'"+near.getText() + "'," + Integer.parseInt(pNumber.getText()) + ",'" +
                                finalDate + "'," + "'"+gender + "'," + "'"+email.getText() + "'," + "'"+password.getText() + "')";
                        statement.executeUpdate(insertStr);
                        String createCart= "insert into cart (userID) values (LAST_INSERT_ID())";
                        statement.executeUpdate(createCart);
                        con.commit();
                        con.close();
                        u.showAlert("Success", "Create an account", "Added successfully", Alert.AlertType.INFORMATION);
                    }
                } catch (SQLException throwables) {

                    u.showAlert("Error", "Create an account", throwables.toString(), Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException nfe) {
                u.showAlert("Error","Create an account","Phone number is not digit,try again!", Alert.AlertType.ERROR);

            }


        }
    }
    @FXML
    public void switchToSignUp(ActionEvent e){
        if(signUp.isSelected())
            account.setVisible(true);
        else
            account.setVisible(false);
    }
    public static void updateCartId(){
        Unit u = new Unit();
        try {
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            String cart = "(select max(cid) from cart where userid = " + userID + ")";
            ResultSet r = s.executeQuery(cart);
            r.next();
            cartID = r.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
