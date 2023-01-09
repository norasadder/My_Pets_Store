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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MyOrdersController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<ordersTable> ordersTableView ;
    @FXML
    private TableColumn<ordersTable,Integer> oidColumn;
    @FXML
    private TableColumn <ordersTable, String> dateColumn ;
    @FXML
    private TableColumn<ordersTable,Integer> priceColumn;
    @FXML
    private Circle pCircle , oCircle , dCircle;
    @FXML
    private TableView<orderProductTable> orderProductTableView;
    @FXML
    private TableColumn<orderProductTable,Integer> pidColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> ppriceColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> quantityColumn;

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

        oidColumn.setCellValueFactory(new PropertyValueFactory< ordersTable,Integer>("OID"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<ordersTable,Integer>("totalPrice"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<ordersTable,String>("date"));
        ordersTableView.setItems(getOrders());

    }
    @FXML
    public void getTheSelection(ActionEvent event){

        pidColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("PID"));
        ppriceColumn.setCellValueFactory(new PropertyValueFactory<orderProductTable,Integer>("totalPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("quantity"));
        orderProductTableView.setItems(getProducts());

    }

    public ObservableList<ordersTable> getOrders() {
        ObservableList<ordersTable> ordersList = FXCollections.observableArrayList() ;
        Unit u = new Unit();
        String orders;
        try{
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            orders = "select oid , odate , totalprice\n" +
                    "from orders \n" +
                    "where cid in (select cid from cart where userid = "+LogOutController.userID+")";
            ResultSet rs = s.executeQuery(orders);
            while (rs.next()) {
                ordersList.add(new ordersTable((rs.getInt("oid")), rs.getInt("totalprice"),rs.getDate("odate").toString()));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ordersList;
    }


    public ObservableList<orderProductTable> getProducts() {
        ObservableList<orderProductTable> productsList = FXCollections.observableArrayList() ;
        Unit u = new Unit();
        ordersTable myOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (myOrder==null){
            u.showAlert("Error","Product table","Order id is undefined", Alert.AlertType.ERROR);
        }
        else {
            int orderID = myOrder.getOID();
            String productStr;
            String trackStr;
            try {
                Connection con = u.mySQLConnect();
                Statement s = con.createStatement();
                productStr = "select pid , quantity , totalprice\n" +
                        "from CART_PRODUCT \n" +
                        "where cid in (select cid from orders where oid = " + Integer.toString(orderID) + ")";
                ResultSet rs = s.executeQuery(productStr);
                while (rs.next()) {
                    productsList.add(new orderProductTable((rs.getInt("pid")), rs.getInt("totalprice"), rs.getInt("quantity")));
                }
                trackStr = "select processing,ontheway,delivered\n" +
                        "from orders \n" +
                        "where oid = " + Integer.toString(orderID);
                System.out.println(Integer.toString(orderID));
                ResultSet r = s.executeQuery(trackStr);
                r.next();
                if (r.getInt("processing") == 1)
                    pCircle.setFill(Color.GREEN);
                else pCircle.setFill(Color.RED);
                if (r.getInt("ontheway") == 1)
                    oCircle.setFill(Color.GREEN);
                else oCircle.setFill(Color.RED);
                if (r.getInt("delivered") == 1)
                    dCircle.setFill(Color.GREEN);
                else dCircle.setFill(Color.RED);
            } catch (SQLException throwables) {
                throwables.toString();
            }
        }
        return productsList;
    }
}
