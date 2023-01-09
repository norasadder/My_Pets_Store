package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EPetsController {

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField pid ;

    @FXML
    private TextField category;

    @FXML
    private TextField petType;
    @FXML
    private TextField price;
    @FXML
    private TextField quantity;
    @FXML
    private TextField medInfo;
    @FXML
    private TextField description;

    @FXML
    private DatePicker birthdate;

    @FXML
    private JFXRadioButton feMale,male;

    @FXML
    private CheckBox chb ;

    char g ;

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
        Parent root = FXMLLoader.load(getClass().getResource(name));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    public void addItem () {
        Unit u = new Unit() ;
        if (chb.isSelected()) {
            u.showAlert("Error","Add Pet","You can't enter new product id!", Alert.AlertType.ERROR);
        }
        else {
        if(category.getText().isEmpty()||petType.getText().isEmpty()||price.getText().isEmpty()
                ||medInfo.getText().isEmpty()||description.getText().isEmpty()||birthdate.getValue()==null||
                ((!male.isSelected())&&(!feMale.isSelected()))||
                quantity.getText().isEmpty()){
            u.showAlert("Error","Add Pet","Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
        }
        else {
            try {
                if (feMale.isSelected()) {
                    g = 'F';
                } else if (male.isSelected()) {
                    g = 'M';
                }
                String addItemString;
                Connection con = u.mySQLConnect();
                Statement st = con.createStatement();

                addItemString = "insert into product (P_Price,P_Quantity) values (" + price.getText() + "," + quantity.getText() + ")";
//                addItemString = "insert into product values (PID.nextVal,"+price.getText()+","+quantity.getText()+")";
                st.executeUpdate(addItemString);
                addItemString = "insert into pets (PID,Category , petType , BDate , Gender,MedSituation,petDescription) values (LAST_INSERT_ID(),'" +
                        category.getText() + "','" + petType.getText() + "','" + birthdate.getValue() + "','" + g + "','" +
                        medInfo.getText() + "','" + description.getText() + "')";
//                addItemString = "insert into pets values (PetSerial.nextVal,PID.currval,'" +
//                       category.getText() + "','" + petType.getText() + "','" + birthdate.getValue() + "','" +g+"','"+
//                        medInfo.getText()+"','"+description.getText()+"')" ;
                st.executeUpdate(addItemString);
                con.close();
            } catch (SQLException ex) {

            }
        }
        }
    }

    public void deleteItem (){
        Unit u = new Unit() ;
        int productID ;
        if(!chb.isSelected()) {
            u.showAlert("Error","delete Pet","Please enter product id!", Alert.AlertType.ERROR);
        }
        else {
            try {
                productID = Integer.parseInt(pid.getText());
                Connection con = u.mySQLConnect();
                Statement st = con.createStatement();
                String deleteSt = "delete from product where pid = "+productID ;
                st.executeUpdate(deleteSt) ;
                con.close();
            } catch (SQLException ex) {
            }
        }
    }

public void setEditable () {
        if(chb.isSelected()) {
        pid.setEditable(true); }
        else {
            pid.setEditable(false);
        }

}

public void searchItem () {
    Unit u = new Unit() ;
    LocalDate date ;
    int productID ;
    if(!chb.isSelected()) {
        u.showAlert("Error","Search Pet","Please enter product id!", Alert.AlertType.ERROR);
    }
    else {
        try {
            productID = Integer.parseInt(pid.getText());
            Connection con = u.mySQLConnect();
            Statement st = con.createStatement();
            String searchSt = "select Product.PID , Pets.category , Pets.petType , product.P_Price , product.P_Quantity , Pets.BDate , Pets.Gender , Pets.medSituation , Pets.petDescription " +
                    "from pets,product where pets.PID = product.PID and  product.PID = " + productID;
            ResultSet rs = st.executeQuery(searchSt);
            rs.next() ;
            pid.setText(rs.getString(1));
            category.setText(rs.getString(2));
            petType.setText(rs.getString(3));
            price.setText(rs.getString(4));
            quantity.setText(rs.getString(5));
            date = LocalDate.parse(rs.getString(6)) ;
            birthdate.setValue(date);
            if(rs.getString(7).equals('F')) {
             feMale.setSelected(true);
                male.setSelected(false);
            }
            else {
                male.setSelected(true);
                feMale.setSelected(false);
            }
            medInfo.setText(rs.getString(8));
            description.setText(rs.getString(9));
            con.close();
        } catch (SQLException ex) {
        }
    }
}


public void updateItem() {
    Unit u = new Unit() ;
    LocalDate date ;
    String updateSt;
    int productID ;
    try {
        productID = Integer.parseInt(pid.getText());
        Connection con = u.mySQLConnect();
        Statement statement = con.createStatement();
        Statement st = con.createStatement() ;
        if (!chb.isSelected()) {
            u.showAlert("Error", "Update Pet", "Please enter product id!", Alert.AlertType.ERROR);
        } else {
            if (category.getText().isEmpty() || petType.getText().isEmpty() || price.getText().isEmpty()
                    || medInfo.getText().isEmpty() || description.getText().isEmpty() || birthdate.getValue() == null ||
                    ((!male.isSelected()) && (!feMale.isSelected())) ||
                    quantity.getText().isEmpty()) {
                u.showAlert("Error", "Update Pet", "Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
            } else {
                if (feMale.isSelected()) {
                    g = 'F';
                } else if (male.isSelected()) {
                    g = 'M';
                }
                updateSt = "update Product set P_Price = " + price.getText() + ",P_Quantity = " + quantity.getText() + " where PID = " + productID;
                statement.executeUpdate(updateSt);
                updateSt = "update pets set petType = '" + petType.getText()+"', category = '" +category.getText()+"', BDate = '" +birthdate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"',Gender = '"+g+"', medSituation = '"+
                medInfo.getText()+"', petDescription = '"+description.getText()+"' where PID = "+productID;
                st.executeUpdate(updateSt);
            }
        }
        con.close();
    } catch (SQLException ex) {


    }
    }

}