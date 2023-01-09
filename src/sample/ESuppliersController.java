package sample;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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

public class ESuppliersController {
    @FXML
    private AnchorPane pane;
    @FXML
    private CheckBox chb ;

    @FXML
    private TextField pid ;

    @FXML
    private TextField sid;
    @FXML
    private TextField price ;
    @FXML
    private TextField phone ;

    @FXML
    private TextField name ;

    @FXML
    private TextField quantity;

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
        String name =LogOutController.flag+((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml";
        if (name.contains("Logout")){
            name = "Logout.fxml";
        }
        Parent root = FXMLLoader.load(getClass().getResource(name));
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
            u.showAlert("Error","Add Supplier","You can't enter new supplier id!", Alert.AlertType.ERROR);
        }
        else {
            if(pid.getText().isEmpty()||price.getText().isEmpty() || quantity.getText().isEmpty()||phone.getText().isEmpty()||name.getText().isEmpty()){
                u.showAlert("Error","Add Supplier","Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
            }
            else {
                try {
                    String addItemString;
                    Connection con = u.mySQLConnect();
                    Statement st = con.createStatement();
                    addItemString = "insert into supplier (SName,SPhone) values ('" + name.getText() + "'," + phone.getText() + ")";
//                addItemString = "insert into supplier values (SID.nextVal,'"+SName.getText()+"',"+SPhone.getText()+")";
                    st.executeUpdate(addItemString);
                    addItemString = "insert into supplier_product (SID,PID,S_Quantity,S_Price) values (LAST_INSERT_ID()," +pid.getText()+","+quantity.getText()+","+price.getText()+ ")";
//                addItemString = "insert into supplier values (Supplier_Product_SN.nextVal,SID.currval," +pid.getText()+","+quantity.getText()+","+price.getText()+")" ;
                    st.executeUpdate(addItemString);
                    con.close();
                } catch (SQLException ex) {
                }
            }
        }
    }


    public void deleteItem (){
        Unit u = new Unit() ;
        int supplierID ;
        if(!chb.isSelected()) {
            u.showAlert("Error","delete Supplier","Please enter supplier id!", Alert.AlertType.ERROR);
        }
        else {
            try {
                supplierID = Integer.parseInt(sid.getText());
                Connection con = u.mySQLConnect();
                Statement st = con.createStatement();
                String deleteSt = "delete from supplier where sid = "+supplierID ;
                st.executeUpdate(deleteSt) ;
                con.close();
            } catch (SQLException ex) {
            }
        }
    }

    public void setEditable () {
        if (chb.isSelected()) {
            sid.setEditable(true);
        } else {
            sid.setEditable(false);
        }
    }

        public void searchItem() {
            Unit u = new Unit() ;
            int supplierID ;
            if(!chb.isSelected()) {
                u.showAlert("Error","Search Supplier","Please enter supplier id!", Alert.AlertType.ERROR);
            }
            else {
                try {
                    supplierID = Integer.parseInt(sid.getText());
                    Connection con = u.mySQLConnect();
                    Statement st = con.createStatement();
                    String searchSt = "select supplier.SName,Supplier.SPhone,Supplier_Product.PID,supplier_product.S_Price,supplier_product.S_Quantity " +
                            "from supplier,supplier_product where supplier.SID = supplier_product.SID and  supplier.SID = " + supplierID;
                    ResultSet rs = st.executeQuery(searchSt);
                    rs.next() ;
                    name.setText(rs.getString(1));
                    phone.setText(rs.getString(2));
                    pid.setText(rs.getString(3));
                    price.setText(rs.getString(4));
                    quantity.setText(rs.getString(5));
                    con.close();
                } catch (SQLException ex) {
                }
            }

        }


    public void updateItem() {
        Unit u = new Unit() ;
        String updateSt;
        int supplierID ;
        try {
            supplierID = Integer.parseInt(sid.getText());
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
            Statement st = con.createStatement() ;
            if (!chb.isSelected()) {
                u.showAlert("Error", "Update supplier", "Please enter supplier id!", Alert.AlertType.ERROR);
            } else {
                if (name.getText().isEmpty() || price.getText().isEmpty()
                        || quantity.getText().isEmpty()||pid.getText().isEmpty()||phone.getText().isEmpty()) {
                    u.showAlert("Error", "Update supplier", "Please enter all the requirement field and try again!", Alert.AlertType.ERROR);
                } else {
                    updateSt = "update supplier set SName = '" + name.getText() + "',SPhone = '" + phone.getText() + "' where SID = " + supplierID;
                    statement.executeUpdate(updateSt);
                    updateSt = "update supplier_product set PID = " + pid.getText()+", S_Quantity = "+quantity.getText()+", S_Price = "+price.getText()+" where SID = "+supplierID;

                    st.executeUpdate(updateSt);
                }
            }
            con.close();
        } catch (SQLException ex) {
        }
    }




}
