package sample;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class MyCartController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<orderProductTable> cartTableView;
    @FXML
    private TableColumn<orderProductTable,Integer> cartPidColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> cartPriceColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> cartQuantityColumn;

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
        Parent root = FXMLLoader.load(getClass().getResource(((JFXButton)event.getSource()).getText().replaceAll("\\s", "")+".fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cartPidColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("PID"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<orderProductTable,Integer>("totalPrice"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("quantity"));
        cartTableView.setItems(getCart());
    }
    public ObservableList<orderProductTable> getCart() {
        ObservableList<orderProductTable> cartList = FXCollections.observableArrayList() ;
        Unit u = new Unit();
        String cartStr;
        try {
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            cartStr = "select pid , quantity , totalprice\n" +
                    "from cart_product\n" +
                    "where cid = "+ LogOutController.cartID;
            System.out.println(LogOutController.cartID);
            ResultSet rs = s.executeQuery(cartStr);
            while (rs.next()) {
                cartList.add(new orderProductTable((rs.getInt("pid")), rs.getInt("totalprice"), rs.getInt("quantity")));
            }
        } catch (SQLException throwables) {
            throwables.toString();
        }

        return cartList;
    }

    @FXML
    public void deleteProduct(ActionEvent event){
        orderProductTable pt = cartTableView.getSelectionModel().getSelectedItem();
        cartTableView.getItems().remove(pt);
        int pDelete = pt.getPID();
        int quantity = pt.getQuantity();
        Unit u = new Unit();
        String delStr;
        try {
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            delStr = "delete from cart_product\n"+
                    "where pid = "+pDelete+" and cid = "+LogOutController.cartID+" and quantity = "+quantity;
            s.executeUpdate(delStr);
            con.commit();
            cartTableView.getItems().removeAll();
            cartTableView.setItems(getCart());
        } catch (SQLException throwables) {
            throwables.toString();
        }
    }
    public void orderProducts(){
        Unit u = new Unit();
        String checkStr;
        String order;
        LocalDate date;
        ArrayList<Integer> pid = new ArrayList<Integer>();
        ArrayList<Integer> pQuantity = new ArrayList<Integer>();
        ArrayList<Integer> reqQuantity = new ArrayList<Integer>();
        boolean flag = true;
        try{
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            checkStr = "select cart_product.pid , cart_product.quantity  , product.p_quantity \n" +
                    "from cart_product,product\n" +
                    "where CART_PRODUCT.pid = product.pid and cid = "+LogOutController.cartID;
            ResultSet rs = s.executeQuery(checkStr);
            System.out.println("1");
            while (rs.next()){
                if (rs.getInt(2)>rs.getInt(3)) {
                    u.showAlert("Error", "Order", "The quantity that you order from the product with id =" + rs.getInt(1) + " is more than what is " +
                            "available in our store!", Alert.AlertType.ERROR);
                    flag = false;
                }
                else {
                    pid.add(rs.getInt(1));
                    reqQuantity.add(rs.getInt(2));
                    pQuantity.add(rs.getInt(3));
                }
            }
            if (flag == true){
                date = getDate();
                String totalStr = "select sum(totalprice) from cart_product where cid="+LogOutController.cartID;
                ResultSet set = s.executeQuery(totalStr);
                System.out.println("2");
                set.next();
                int total = set.getInt(1);
                order = "insert into orders (CID,Processing,OnTheWay,delivered,ODate,TotalPrice) values("+LogOutController.cartID+",0,0,0,'"+date+"',"+total+")";
                s.executeUpdate(order);
                System.out.println("3");
                String str = "update product \n" +
                        "set p_quantity = ?\n"+
                        "where pid = ?";
                PreparedStatement ps = con.prepareStatement(str);
                System.out.println("4");
                for (int i=0;i<pid.size();i++){
                    ps.setInt(1,pQuantity.get(i)-reqQuantity.get(i));
                    ps.setInt(2,pid.get(i));
                    ps.executeUpdate();
                    System.out.println("5");
                }
                String newCart = "insert into cart (userID) values ("+LogOutController.userID+")";
                s.executeUpdate(newCart);
//                con.commit();
                cartTableView.getItems().removeAll();
                cartTableView.setItems(getCart());
                LogOutController.updateCartId();
                u.showAlert("Success","Orders","Your order added successfully", Alert.AlertType.INFORMATION);
            }
        }
        catch (SQLException throwables){
            throwables.toString();
        }
    }
    public LocalDate getDate (){
        return java.time.LocalDate.now();
    }
}
