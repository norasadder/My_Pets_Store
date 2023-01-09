package sample;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.ResourceBundle;

public class AccessoriesController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private ChoiceBox accessoriesCategory;
    ObservableList<String> accessories = FXCollections.observableArrayList("All items","cat food","dog food","cat cage","dog cage");
    @FXML
    private Spinner<Integer> accessoriesSpinner;
    @FXML
    private TableView<accessoriesTable> accessoriesTableView ;
    @FXML
    private TableColumn <accessoriesTable,Integer> pidColumn;
    @FXML
    private TableColumn <accessoriesTable,String> accessoriesTypeColumn ;
    @FXML
    private TableColumn <accessoriesTable,Integer>  priceColumn ;
    @FXML
    private Label accessoriesPriceLabel ;
    @FXML
    private Label accessoriesTypeLabel ;

    @FXML
    private Slider accessoriesPriceSlider ;

    String accessoriesChoiceBoxValue ;

    int accessoriesPriceRange ;
    int accessoriesQuantity ;

    @FXML
    private ImageView accessoriesImageView ;

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
        accessoriesCategory.setItems(accessories);
        accessoriesCategory.setValue("All items");
        pidColumn.setCellValueFactory(new PropertyValueFactory< accessoriesTable,Integer>("PID"));
        accessoriesTypeColumn.setCellValueFactory(new PropertyValueFactory<accessoriesTable,String>("accessoriesType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<accessoriesTable,Integer>("price"));
        accessoriesTableView.setItems(getAccessories());

        accessoriesPriceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                accessoriesPriceRange = (int) accessoriesPriceSlider.getValue() ;
            }
        });

    }

    public ObservableList<sample.accessoriesTable> getAccessories() {
        ObservableList<accessoriesTable> accessoriesList = FXCollections.observableArrayList() ;
        Unit u = new Unit () ;
        try {
            Connection con = u.mySQLConnect() ;
            Statement statement = con.createStatement();
            String searchStr = "select accessories.PID ,accessories.accessoriesType,Product.P_Price from accessories, Product where accessories.PID = Product.PID ";
            ResultSet rs = statement.executeQuery(searchStr) ;
            while(rs.next()) {
                accessoriesList.add(new accessoriesTable(rs.getInt("accessories.PID"),rs.getString("accessories.accessoriesType"),rs.getInt("Product.P_Price"))) ;
            }
        }
        catch (SQLException ex) {
        }
        return accessoriesList;
    }

    public void showAccessoriesDetails () {
        accessoriesTable accessoriestable = accessoriesTableView.getSelectionModel().getSelectedItem() ;
        accessoriesTypeLabel.setText(accessoriestable.getAccessoriesType());
        accessoriesPriceLabel.setText(String.valueOf(accessoriestable.getPrice()));
        int accessoriesID = accessoriestable.getPID();
//       Image accessoriesImage = new Image(getClass().getResourceAsStream(String.valueOf(accessoriesID)+".jpg")) ;
        Unit u = new Unit();
        try {
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
            String quantitySearch = "select P_Quantity from Product where PID = "+String.valueOf(accessoriesID) ;
            ResultSet rs = statement.executeQuery(quantitySearch) ;
            rs.next();
            accessoriesQuantity = rs.getInt("P_Quantity") ;

            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,accessoriesQuantity) ;
            valueFactory.setValue(0);
            accessoriesSpinner.setValueFactory(valueFactory);
            Image accessoriesImage = new Image(getClass().getResourceAsStream("/Picture/"+accessoriestable.getPID()+".jpg"));
            accessoriesImageView.setImage(accessoriesImage);
            con.close();;
        } catch (SQLException exception) {


        }
    }

    public void filter () {
        accessoriesTableView.setItems(getFilteredAccessories());
    }


    public ObservableList<sample.accessoriesTable> getFilteredAccessories() {
        ObservableList<accessoriesTable> accessoriesList = FXCollections.observableArrayList() ;
        Unit u = new Unit () ;
        accessoriesChoiceBoxValue = (String)accessoriesCategory.getValue() ;
        try {
            Connection con = u.mySQLConnect() ;
            Statement statement = con.createStatement();
            String searchStr ;
            if(accessoriesPriceRange!=0) {
            if(accessoriesChoiceBoxValue.equals("All items")){
           searchStr = "select accessories.PID ,accessories.accessoriesType,Product.P_Price from accessories, Product where accessories.PID = Product.PID and Product.P_Price between 0 and "+ String.valueOf(accessoriesPriceRange);}
            else { searchStr = "select accessories.PID ,accessories.accessoriesType,Product.P_Price from accessories, Product where accessories.PID = Product.PID and Product.P_Price between 0 and "+ String.valueOf(accessoriesPriceRange)+" and accessories.accessoriesType = '" + accessoriesChoiceBoxValue+"'";
            } }

            else {
                if(accessoriesChoiceBoxValue.equals("All items")){
                    searchStr = "select accessories.PID ,accessories.accessoriesType,Product.P_Price from accessories, Product where accessories.PID = Product.PID ";}
                else { searchStr = "select accessories.PID ,accessories.accessoriesType,Product.P_Price from accessories, Product where accessories.PID = Product.PID  and accessories.accessoriesType = '" + accessoriesChoiceBoxValue+"'";
                }

            }
            ResultSet rs = statement.executeQuery(searchStr) ;
            while(rs.next()) {
                accessoriesList.add(new accessoriesTable(rs.getInt("accessories.PID"),rs.getString("accessories.accessoriesType"),rs.getInt("Product.P_Price"))) ;
            }
        }
        catch (SQLException ex) {
        }
        return accessoriesList;
    }


  public void addToCart () {
        Unit u = new Unit();
        u.mySQLConnect();
      accessoriesTable accessoriestable = accessoriesTableView.getSelectionModel().getSelectedItem() ;
      accessoriesQuantity = accessoriesSpinner.getValue() ;
        try {
            if(accessoriesQuantity==0){
                u.showAlert("Error","Add item to cart","Please enter the quantity and try again!", Alert.AlertType.ERROR);
            }
            else {
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
//             String ADD = "insert into cart_product values (Cart_product_SN.nextVal,"+accessoriestable.getPID()+","+"3"+","+accessoriesQuantity*accessoriestable.getPrice()+","+accessoriesQuantity+")";
            String ADD = "insert into cart_product (PID,CID,TotalPrice,Quantity) values ("+accessoriestable.getPID()+","+LogOutController.cartID+","+accessoriesQuantity*accessoriestable.getPrice()+","+accessoriesQuantity+")";
          statement.executeUpdate(ADD) ;
            con.close();
            }
        } catch (SQLException ex) {

        }


  }



}
