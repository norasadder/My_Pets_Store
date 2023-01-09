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
import javafx.scene.control.CheckBox;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EAccessoriesController {
    @FXML
    private AnchorPane pane;


    @FXML
    private TextField pid ;

    @FXML
    private TextField accessoriesType;
    @FXML
    private TextField price;
    @FXML
    private TextField quantity;

    @FXML
    private CheckBox chb ;


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
        System.out.println(LogOutController.flag+((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml");
        Parent root = FXMLLoader.load(getClass().getResource(LogOutController.flag+((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }


    public void addItem () {
        Unit u = new Unit() ;
        if (chb.isSelected()) {
            u.showAlert("Error","Add Accessories","You can't enter new product id!", Alert.AlertType.ERROR);
        }
        else {
            if(accessoriesType.getText().isEmpty()||price.getText().isEmpty() || quantity.getText().isEmpty()){
                u.showAlert("Error","Add Accessories","Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
            }
            else {
                try {
                    String addItemString;
                    Connection con = u.mySQLConnect();
                    Statement st = con.createStatement();
                    addItemString = "insert into product (P_Price,P_Quantity) values (" + price.getText() + "," + quantity.getText() + ")";
//                addItemString = "insert into product values (PID.nextVal,"+price.getText()+","+quantity.getText()+")";
                    st.executeUpdate(addItemString);
                    addItemString = "insert into accessories (PID,accessoriesType) values (LAST_INSERT_ID(),'" +accessoriesType.getText()+ "')";
//                addItemString = "insert into accessories values (AccessoriesSerial.nextVal,PID.currval,'" +accessoriesType.getText()+"')" ;
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
            u.showAlert("Error","delete Accessories","Please enter product id!", Alert.AlertType.ERROR);
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
        int productID ;
        if(!chb.isSelected()) {
            u.showAlert("Error","Search Accessories","Please enter product id!", Alert.AlertType.ERROR);
        }
        else {
            try {
                productID = Integer.parseInt(pid.getText());
                Connection con = u.mySQLConnect();
                Statement st = con.createStatement();
                String searchSt = "select Product.PID , accessories.accessoriesType , product.P_Price , product.P_Quantity " +
                        "from accessories,product where accessories.PID = product.PID and  product.PID = " + productID;
                ResultSet rs = st.executeQuery(searchSt);
                rs.next() ;
                pid.setText(rs.getString(1));
                accessoriesType.setText(rs.getString(2));
                price.setText(rs.getString(3));
                quantity.setText(rs.getString(4));
                con.close();
            } catch (SQLException ex) {
            }
        }

    }


    public void updateItem() {
        Unit u = new Unit() ;
        String updateSt;
        int productID ;
        try {
            productID = Integer.parseInt(pid.getText());
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
            Statement st = con.createStatement() ;
            if (!chb.isSelected()) {
                u.showAlert("Error", "Update accessories", "Please enter product id!", Alert.AlertType.ERROR);
            } else {
                if (accessoriesType.getText().isEmpty() || price.getText().isEmpty()
                        || quantity.getText().isEmpty()) {
                    u.showAlert("Error", "Update accessories", "Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
                } else {
                    updateSt = "update product set P_Price = " + price.getText() + ",P_Quantity = " + quantity.getText() + " where PID = " + productID;
                    statement.executeUpdate(updateSt);
                    updateSt = "update accessories set accessoriesType = '" + accessoriesType.getText()+"' where PID = "+productID;
                    st.executeUpdate(updateSt);
                }
            }
            con.close();
        } catch (SQLException ex) {
        }
    }

}
