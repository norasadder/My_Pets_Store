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

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PetsController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private ChoiceBox petsCategory;
    ObservableList<String> pets = FXCollections.observableArrayList("All items","Cat","Dog");
    @FXML
    private Spinner <Integer> petsSpinner;
    @FXML
    private TableView <petsTable> petsTableView ;
    @FXML
    private  TableColumn <petsTable,String>  categoryColumn ;
    @FXML
    private TableColumn <petsTable,Integer> pidColumn;
    @FXML
    private TableColumn <petsTable,String> petTypeColumn ;
    @FXML
    private TableColumn <petsTable,Integer>  priceColumn ;
    @FXML
    private TableColumn <petsTable,String> birthdateColumn ;
    @FXML
    private TableColumn <petsTable,String>  genderColumn ;
    @FXML
    private Label medInfoLabel ;
    @FXML
    private Label petDescriptionLabel ;
    @FXML
    private Label petPriceLabel ;
    @FXML
    private Label petTypeLabel ;

    @FXML
    private ImageView petImageView ;

    Image petImage ;

    @FXML
    private Slider petsPriceSlider ;

    int petsPriceRange ;

    String petsChoiceBoxValue ;


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
        petsCategory.setItems(pets);
        petsCategory.setValue("All items");
        pidColumn.setCellValueFactory(new PropertyValueFactory< petsTable,Integer>("PID"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory< petsTable,String>("category"));
        petTypeColumn.setCellValueFactory(new PropertyValueFactory<petsTable,String>("petType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<petsTable,Integer>("price"));
        birthdateColumn.setCellValueFactory(new PropertyValueFactory<petsTable,String>("birthdate"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<petsTable, String>("gender")) ;
        petsTableView.setItems(getPets());

        petsPriceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                petsPriceRange = (int) petsPriceSlider.getValue() ;
            }
        });
        }

    public ObservableList<sample.petsTable> getPets() {
        ObservableList<petsTable> petsList = FXCollections.observableArrayList() ;
       Unit u = new Unit () ;
       try {
       Connection con = u.mySQLConnect() ;
        Statement statement = con.createStatement();
        String searchStr = "select Pets.PID , Pets.Category , Pets.petType,Product.P_Price , Pets.BDate , Pets.Gender from Pets , Product where Pets.PID = Product.PID ";
        ResultSet rs = statement.executeQuery(searchStr) ;
        while(rs.next()) {
          petsList.add(new petsTable(rs.getInt("Pets.PID"),rs.getString("Pets.Category"),rs.getString("Pets.PetType"),rs.getInt("Product.P_Price"),rs.getString("Pets.BDate"),rs.getString("Pets.Gender"))) ;
        }
       }
       catch (SQLException ex) {
       }
        return petsList;
    }

    int petQuantity ;
    public void showPetsDetails () {
        petsTable petstable = petsTableView.getSelectionModel().getSelectedItem() ;
        petTypeLabel.setText(petstable.getPetType());
        petPriceLabel.setText(String.valueOf(petstable.getPrice()));
        Unit u = new Unit();
        int petID = petstable.getPID();
        try {
            Connection con = u.mySQLConnect();
            Statement statement = con.createStatement();
            String searchStr = "select medSituation , petDescription from pets where PID = "+String.valueOf(petID);
            String quantitySearch = "select P_Quantity from Product where PID = "+String.valueOf(petID) ;
            ResultSet rs = statement.executeQuery(searchStr) ;
            rs.next();
            medInfoLabel.setText(rs.getString("medSituation"));
            petDescriptionLabel.setText(rs.getString("petDescription"));

            rs = statement.executeQuery(quantitySearch) ;
            rs.next();
            petQuantity = rs.getInt("P_Quantity") ;

            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,petQuantity) ;
            valueFactory.setValue(0);
            petsSpinner.setValueFactory(valueFactory);

            Image petImage = new Image(getClass().getResourceAsStream("/Picture/"+petstable.getPID()+".jpg"));
            petImageView.setImage(petImage);
            con.close();
        }
        catch (SQLException ex) {


        }

    }

    public void filter () {
        petsTableView.setItems(getFilteredPets());
    }


    public ObservableList<sample.petsTable> getFilteredPets() {
        ObservableList<petsTable> petsList = FXCollections.observableArrayList() ;
        Unit u = new Unit () ;
        petsChoiceBoxValue = (String)petsCategory.getValue() ;
        try {
            Connection con = u.mySQLConnect() ;
            Statement statement = con.createStatement();
            String searchStr ;

            if(petsPriceRange!=0) {
                if( petsChoiceBoxValue.equals("All items")){
                    searchStr = "select Pets.PID , Pets.Category , Pets.petType,Product.P_Price , Pets.BDate , Pets.Gender from Pets , Product where Pets.PID = Product.PID and Product.P_Price between 0 and  "+ String.valueOf(petsPriceRange);}
                else { searchStr = "select Pets.PID , Pets.Category , Pets.petType,Product.P_Price , Pets.BDate , Pets.Gender from Pets , Product where Pets.PID = Product.PID and Product.P_Price between 0 and  "+ String.valueOf(petsPriceRange) + " and Pets.Category = '" + petsChoiceBoxValue+"'";
                } }

            else {
                if( petsChoiceBoxValue.equals("All items")){
                    searchStr = "select Pets.PID , Pets.Category , Pets.petType,Product.P_Price , Pets.BDate , Pets.Gender from Pets , Product where Pets.PID = Product.PID ";}
                else { searchStr = "select Pets.PID , Pets.Category , Pets.petType,Product.P_Price , Pets.BDate , Pets.Gender from Pets , Product where Pets.PID = Product.PID and Pets.Category = '"+petsChoiceBoxValue+"'";
                }

            }

            ResultSet rs = statement.executeQuery(searchStr) ;
            while(rs.next()) {
                petsList.add(new petsTable(rs.getInt("Pets.PID"),rs.getString("Pets.Category"),rs.getString("Pets.PetType"),rs.getInt("Product.P_Price"),rs.getString("Pets.BDate"),rs.getString("Pets.Gender"))) ;
            }
        }
        catch (SQLException ex) {
        }
        return petsList;
    }


    public void addToCart () {
        Unit u = new Unit();
        u.mySQLConnect();
        petsTable petstable = petsTableView.getSelectionModel().getSelectedItem() ;
        petQuantity = petsSpinner.getValue() ;
        try {
            if(petQuantity==0){
                u.showAlert("Error","Add item to cart","Please enter the quantity and try again!", Alert.AlertType.ERROR);
            }
            else {
                Connection con = u.mySQLConnect();
                Statement statement = con.createStatement();
//                String ADD = "insert into cart_product values (Cart_product_SN.nextVal ,"+petstable.getPID()+","+"3"+","+petQuantity*petstable.getPrice()+","+petQuantity+")";
                String ADD = "insert into cart_product (PID,CID,TotalPrice,Quantity) values ("+petstable.getPID()+","+LogOutController.cartID+","+petQuantity*petstable.getPrice()+","+petQuantity+")";
                statement.executeUpdate(ADD) ;
                con.close();
            }
        } catch (SQLException ex) {

        }


    }




}
