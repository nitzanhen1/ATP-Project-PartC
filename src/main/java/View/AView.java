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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.util.Observable;

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
        //if(true)
        //    return;
        if(player!=null)
            player.pause();
        player = new MediaPlayer(song);
        playMusic();
    }

    public void playMusic(){
        //player.play();
        player.setAutoPlay(musicState);
        player.setVolume(0.4);
        player.setOnEndOfMedia(new Runnable() {
            public void run() {
                player.seek(Duration.ZERO);
            }
        });
    }

    public void setStage(Stage Stage) {
        this.stage = Stage;
    }

    public void setChosenChar(String chosenChar) {
        this.chosenChar = chosenChar;
    }

    public String getChosenChar() {
        return chosenChar;
    }


    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
    }

    protected void changeScene(Stage stage, String fxmlName, String cssName, double width, double height){
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
        changeScene(stage,"/MyView.fxml","/MyStyle.css", stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }

    public void saveGame(ActionEvent actionEvent) {
        /**
         * save maze to a file
         * we use the a file choose and the player choose the location and the name of the maze
         */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File savedFile = fileChooser.showSaveDialog(null);

        if (savedFile != null) {
            myViewModel.saveMaze(savedFile);
            showAlert(Alert.AlertType.INFORMATION,"saved successfully", "Maze: " + savedFile.getName()+" has been saved.");
        }
    }

    public void loadGame(ActionEvent actionEvent) {
        /**
         * function to load a maze
         * The player choose a maze file to load to the screen
         */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File file= fileChooser.showOpenDialog(stage);
        newGame(actionEvent);
        this.myViewModel.loadMaze(file);
    }

    public void properties(ActionEvent actionEvent) {
            Stage newStage = new Stage();
            newStage.setTitle("Properties");
            changeScene(newStage,"/Properties.fxml","/MainStyle.css",400,300);
    }

    public void help(MouseEvent mouseEvent) {
            Stage newStage = new Stage();
            newStage.setTitle("Help");
            changeScene(newStage,"/Help.fxml","/MainStyle.css",700,600);
    }

    public void about(MouseEvent mouseEvent) {
        String content = "Hey, We are Nitzan Hen and Malka Hanimov.\n\n" +
                "We created a maze game based on the Friends show \n\n" +
                "the maze is generated based on prim's algorithm \n\n" +
                "the maze solveMaze option is based on BFS, DFS or BestFirstSearch\n\n"+
                "enjoy :)";
        showAlert(Alert.AlertType.INFORMATION,"about",content);
    }

    public void exit() {
        myViewModel.exit();
        Platform.exit();
    }

    public void muteUnmute(ActionEvent actionEvent) {
        if(musicState)
            player.pause();
        else
            player.play();
        musicState=!musicState;
    }

    public void showAlert(Alert.AlertType type,String title, String message)
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void backToMain(ActionEvent actionEvent) {
        if(stage.getScene().getStylesheets().get(0).contains("MyStyle"))
            alreadyPlay=false;
        changeScene(stage,"/MainView.fxml","/MainStyle.css",stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
    }
    
    public void saveConfigurations(ActionEvent actionEvent) {
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
