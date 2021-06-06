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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    

    public void setStage(Stage Stage) {
        this.stage = Stage;
    }

    public void setChosenChar(String chosenChar) {
        this.chosenChar = chosenChar;
    }

    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
    }

    protected void changeScene(String fxmlName, String cssName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, stage.getScene().widthProperty().getValue(), stage.getScene().heightProperty().getValue());
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
        changeScene("/MyView.fxml","/MyStyle.css");
    }

    public void saveGame(ActionEvent actionEvent) {
        /**
         * save maze to a file
         * we use the a file choose and the player choose the location and the name of the maze
         */
//        if(myViewModel.getMaze()==null) {
//            showAlert("Please generate a maze before saving.");
//        }
//        else{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File savedFile = fileChooser.showSaveDialog(null);

        if (savedFile != null) {
            myViewModel.saveMaze(savedFile);
            showAlert("action success", "Maze: " + savedFile.getName()+" has been saved.");
        }
        else {//not sure if needed
            showAlert("action canceled", "Maze saving was canceled");
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
        if(file==null) //not sure if needed , on cancel show this alert
        {
            showAlert("action canceled", "Maze loading canceled");
            return;
        }
        newGame(actionEvent);
        this.myViewModel.loadMaze(file);
    }

    public void properties(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            newStage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Properties.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/MainStyle.css").toExternalForm());
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void help(MouseEvent mouseEvent) {
        try {
            Stage newStage = new Stage();
            newStage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Help.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets().add(getClass().getResource("/MainStyle.css").toExternalForm());
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void about(MouseEvent mouseEvent) {
        String content = "Hey, We are Nitzan Hen and Malka Hanimov.\n\n" +
                "We created a maze game based on the Friends show \n\n" +
                "the maze is generated based on prim's algorithm \n\n" +
                "the maze solveMaze option is based on BFS, DFS or BestFirstSearch\n\n"+
                "enjoy :)";
        showAlert("about",content);
    }

    public void exit() {
        myViewModel.exit();
        Platform.exit();
    }

    public void muteUnmute(ActionEvent actionEvent) {

    }

    public void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void backToMain(ActionEvent actionEvent) {
        changeScene("/MainView.fxml","/MainStyle.css");
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
                showAlert("Illegal input", "must insert positive number for num of threads");
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
