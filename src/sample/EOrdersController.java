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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class EOrdersController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<orderDetailsTable> ordersTableView ;
    @FXML
    private TableColumn<orderDetailsTable,Integer> oidColumn;
    @FXML
    private TableColumn <orderDetailsTable, String> dateColumn ;
    @FXML
    private TableColumn<orderDetailsTable,Integer> priceColumn;
    @FXML
    private TableColumn<orderDetailsTable,Integer> pColumn;
    @FXML
    private TableColumn<orderDetailsTable,Integer> oColumn;
    @FXML
    private TableColumn<orderDetailsTable,Integer> dColumn;
    @FXML
    private TableView<orderProductTable> orderProductTableView;
    @FXML
    private TableColumn<orderProductTable,Integer> pidColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> ppriceColumn;
    @FXML
    private TableColumn<orderProductTable,Integer> quantityColumn;
    @FXML
    private CheckBox pCB;
    @FXML
    private CheckBox oCB;
    @FXML
    private CheckBox dCB;
    @FXML
    private TextField oidScene;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        oidColumn.setCellValueFactory(new PropertyValueFactory< orderDetailsTable,Integer>("OID"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<orderDetailsTable,Integer>("totalPrice"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<orderDetailsTable,String>("date"));
        pColumn.setCellValueFactory(new PropertyValueFactory<orderDetailsTable,Integer>("p"));
        oColumn.setCellValueFactory(new PropertyValueFactory<orderDetailsTable,Integer>("o"));
        dColumn.setCellValueFactory(new PropertyValueFactory<orderDetailsTable,Integer>("d"));
        ordersTableView.setItems(getOrders());
    }
    @FXML
    public void getTheSelection(ActionEvent event){

        pidColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("PID"));
        ppriceColumn.setCellValueFactory(new PropertyValueFactory<orderProductTable,Integer>("totalPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory< orderProductTable,Integer>("quantity"));
        orderProductTableView.setItems(getProducts());

    }
    public ObservableList<orderDetailsTable> getOrders() {
        ObservableList<orderDetailsTable> ordersList = FXCollections.observableArrayList() ;
        Unit u = new Unit();
        String orders;
        try{
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            orders = "select oid , odate , totalprice,processing,ontheway,DELIVERED\n" +
                    "from orders ";
            ResultSet rs = s.executeQuery(orders);
            while (rs.next()) {
                ordersList.add(new orderDetailsTable((rs.getInt(1)), rs.getInt(3),rs.getDate(2).toString()
                ,rs.getInt(4),rs.getInt(5),rs.getInt(6)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ordersList;
    }

    public ObservableList<orderProductTable> getProducts() {
        ObservableList<orderProductTable> productsList = FXCollections.observableArrayList() ;
        Unit u = new Unit();
        orderDetailsTable myOrder = ordersTableView.getSelectionModel().getSelectedItem();
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

                con.commit();
            } catch (SQLException throwables) {
                throwables.toString();
            }
        }
        return productsList;
    }
    @FXML
    public void getTrack(ActionEvent event) {

        Unit u = new Unit();
        int orderID = Integer.parseInt(oidScene.getText());
        String track;
        if(oidScene.getText().isEmpty())
            u.showAlert("Error","Update Track","Please enter order id you want to update", Alert.AlertType.ERROR);
        else{
        try {
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();;
            if (pCB.isSelected()) {



                track = "update orders \n" +
                        "set processing = 1\n" +
                        "where oid = " + orderID;
               s.executeUpdate(track);}

            if (oCB.isSelected()) {

                         track = "update orders \n" +
                        "set ontheway = 1\n" +
                        "where oid = " + orderID;
                s.executeUpdate(track);}

            if (dCB.isSelected()) {



                track = "update orders \n" +
                        "set delivered = 1\n" +
                        "where oid = " + orderID;
                s.executeUpdate(track);}
            con.commit();
            ordersTableView.getItems().removeAll();
            ordersTableView.setItems(getOrders());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }}
    }
    @FXML
    public void deleteOrder(ActionEvent event){
        int orderID = Integer.parseInt(oidScene.getText());
        Unit u = new Unit();
        try{
            Connection con = u.mySQLConnect();
            Statement s = con.createStatement();
            String delStr = "delete from orders where oid ="+orderID;
            s.executeUpdate(delStr);
            con.commit();
            con.close();
            ordersTableView.getItems().removeAll();
            ordersTableView.setItems(getOrders());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
