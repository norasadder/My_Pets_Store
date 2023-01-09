package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import java.sql.*;


public class Unit {

    private Connection con ;

    public void showAlert(String title,String header, String text, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.showAndWait();
    }

//    public Connection oracleConnect(){
//        try {
//
//            OracleDataSource ods = new OracleDataSource();
//            ods.setURL("jdbc:oracle:thin:@localhost:1521:xe");
//            ods.setUser("c##pets");
//            ods.setPassword("MayarImad##123");
//            con = ods.getConnection();
//            con.setAutoCommit(false);
//
//        } catch (SQLException throwables) {
//           System.out.println(throwables.toString());
//        }
//        return con;
//    }
    public Connection mySQLConnect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "123456");
            }

        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return con;
    }
}
