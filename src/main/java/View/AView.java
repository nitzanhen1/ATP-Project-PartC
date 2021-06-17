package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;

public abstract class AView implements Initializable, IView{
    protected MyViewModel myViewModel;
    protected static Stage stage;
    private String chosenChar="monica";
    public ChoiceBox generate;
    public ChoiceBox solve;
    public TextField threadsNum;
    protected static MediaPlayer player;
    static boolean alreadyPlay=false;
    protected static boolean musicState=true;


    
    public void setMusic(Media song){
        /*set the mediaPlayer with the media, and call playMusic*/
        if(player!=null)
            player.pause();
        player = new MediaPlayer(song);
        playMusic();
    }

    public void playMusic(){
        /*set the properties of the mediaPlayer*/
        player.setAutoPlay(musicState);
        player.setVolume(0.4);
        player.setOnEndOfMedia(new Runnable() { //repeat the music
            public void run() {
                player.seek(Duration.ZERO);
            }
        });
    }

    public void setStage(Stage Stage) {
        this.stage = Stage;
    }

    public void setChosenChar(String chosenChar) {
        /*change the character string*/
        this.chosenChar = chosenChar;
    }

    public String getChosenChar() {
        return chosenChar;
    }


    public void setMyViewModel(MyViewModel myViewModel) {
        /*this function is common to all Views,because when we change scene we need to pass it,or else we lose it*/
        this.myViewModel = myViewModel;
    }

    protected void changeScene(Stage stage, String fxmlName, String cssName, double width, double height){
        /*change the scene by the stage,fxml file,css file that passing as arguments*/
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource(cssName).toExternalForm());

            AView viewController = fxmlLoader.getController();
            viewController.setMyViewModel(myViewModel);
            viewController.setChosenChar(chosenChar);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newGame(ActionEvent actionEvent) {
        /*create a new game change the scene from the main scene to the maze scene*/
        changeScene(stage,"/MyView.fxml","/MyStyle.css", stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }

    public void saveGame(ActionEvent actionEvent) {
        /* save maze to a file, the player choose the location and the name of the maze*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File savedFile = fileChooser.showSaveDialog(null);
        /*show a informative alert*/
        if (savedFile != null) {
            myViewModel.saveMaze(savedFile);
            showAlert(Alert.AlertType.INFORMATION,"saved successfully", "Maze: " + savedFile.getName()+" has been saved.");
        }
    }

    public void loadGame(ActionEvent actionEvent) {
        /* load a maze to a file, the player choose a maze file to load to the screen*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File file= fileChooser.showOpenDialog(stage);
        /*create a new game and load the maze*/
        if (file != null) {
            newGame(actionEvent);
            this.myViewModel.loadMaze(file);
        }

    }

    public void properties(ActionEvent actionEvent) {
        /*create a new stage for the properties screen*/
            Stage newStage = new Stage();
            newStage.setTitle("Properties");
            changeScene(newStage,"/Properties.fxml","/MainStyle.css",400,300);
    }

    public void help(MouseEvent mouseEvent) {
        /*create a new stage for the help screen*/
            Stage newStage = new Stage();
            newStage.setTitle("Help");
            changeScene(newStage,"/Help.fxml","/MainStyle.css",700,600);
    }

    public void about(MouseEvent mouseEvent) {
        /*show an information alert about our maze project*/
        String content = "Hey, We are Nitzan Hen and Malka Hanimov.\n\n" +
                "We created a maze game based on the Friends show \n\n" +
                "the maze is generated based on prim's algorithm \n\n" +
                "the maze solveMaze option is based on BFS, DFS or BestFirstSearch\n\n"+
                "enjoy :)";
        showAlert(Alert.AlertType.INFORMATION,"about",content);
    }

    public void exit() {
        /*call the my model to exit properly*/
        myViewModel.exit();
        Platform.exit();
    }

    public void muteUnmute(ActionEvent actionEvent) {
        /*check if the mediaPlayer is on or off and change it */
        if(musicState)
            player.pause();
        else
            player.play();
        musicState=!musicState;
    }

    public Alert showAlert(Alert.AlertType type,String title, String message) {
        /*show an alert with the properties type,title and message*/
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
        return alert;
    }

    public void backToMain(ActionEvent actionEvent) {
        /*back to main change the scene to the main scene */
        if(stage.getScene().getStylesheets().get(0).contains("MyStyle"))
            alreadyPlay=false;
        changeScene(stage,"/MainView.fxml","/MainStyle.css",stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }
    
    public void saveConfigurations(ActionEvent actionEvent) {
        /*set the properties - threadPoolSize, generateAlgo, solvingAlgo that the player changed at the options, if any */
        int num;
        if(threadsNum.getText()!="") {
            try{
                num = Integer.valueOf(threadsNum.getText());
                if(num>0)
                    Configurations.setThreadPoolSize(num);
                else
                    throw new NumberFormatException();
            }
            catch (NumberFormatException e){
                //show an alert for illegal input of threadPoolSize
                showAlert(Alert.AlertType.WARNING, "Illegal input", "must insert positive number for num of threads");
                return;
            }
        }
        if(generate.getValue()!=null)
            Configurations.setMazeGeneratingAlgorithm(generate.getValue().toString());
        if(solve.getValue()!=null)
            Configurations.setMazeSearchingAlgorithm(solve.getValue().toString());
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }


}
