package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class MainViewController extends AView{

    public javafx.scene.control.Button character;


    Media openSong = new Media(getClass().getResource("../music/openSong.mp3").toExternalForm());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!alreadyPlay) {
            setMusic(openSong);
           alreadyPlay=true;
        }
    }

    public void characterButton(ActionEvent actionEvent) {
        changeScene(stage,"/CharactersView.fxml","/MainStyle.css",stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }


    public void chooseChar(MouseEvent mouseEvent) {
        character = (Button)mouseEvent.getSource();
        setChosenChar(character.getId());

    }

}
