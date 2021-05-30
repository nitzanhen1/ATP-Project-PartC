package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController extends AView{

    public ImageView character;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void characterButton(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CharactersView.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            MainViewController mainViewController = fxmlLoader.getController();
            mainViewController.setMyViewModel(myViewModel);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chooseChar(MouseEvent mouseEvent) {
        character = (ImageView)mouseEvent.getSource();
        character.getId();
        //send id
    }
}
