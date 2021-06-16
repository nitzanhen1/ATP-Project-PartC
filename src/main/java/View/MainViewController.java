package View;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController extends AView{

    public javafx.scene.control.Button character;


    Media openSong = new Media(getClass().getResource("../music/openSong.mp3").toExternalForm());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*at the init we check if mute is on or off for setting the music*/
        if(!alreadyPlay) {
            setMusic(openSong);
           alreadyPlay=true;
        }
    }

    public void characterButton(ActionEvent actionEvent) {
        /*change the scene from the main scene to the character choose scene*/
        changeScene(stage,"/CharactersView.fxml","/MainStyle.css",stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }


    public void chooseChar(MouseEvent mouseEvent) {
        /*get the character name by the button that the player clicked and set it to right according the name */
        character = (Button)mouseEvent.getSource();
        setChosenChar(character.getId());

    }

}
