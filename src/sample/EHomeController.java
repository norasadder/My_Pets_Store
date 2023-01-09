package sample;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class EHomeController {
    @FXML
    private AnchorPane pane;
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
}
