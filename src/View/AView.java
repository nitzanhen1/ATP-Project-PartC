package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;

public abstract class AView implements Initializable, IView{
    protected MyViewModel myViewModel;
    protected static Stage stage;
    private String chosenChar="monica";

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
        changeScene("MyView.fxml","MyStyle.css");
    }

    public void saveGame(ActionEvent actionEvent) {
    }

    public void loadGame(ActionEvent actionEvent) {
    }

    public void properties(ActionEvent actionEvent) {
    }

    public void help(MouseEvent mouseEvent) {
        try {
            Stage newStage = new Stage();
            newStage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Help.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void about(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("about");
        String content = "Hey, We are Nitzan Hen and Malka Hanimov.\n\n" +
                "We created a maze game based on the Friends show \n\n" +
                "the maze is generated based on prim's algorithm \n\n" +
                "the maze solveMaze option is based on BFS, DFS or BestFirstSearch\n\n"+
                "enjoy :)";
        alert.setContentText(content);
        alert.show();
    }

    public void exit() {
        myViewModel.exit();
        Platform.exit();
    }

    public void muteUnmute(ActionEvent actionEvent) {

    }

    public void backToMain(ActionEvent actionEvent) {
        changeScene("MainView.fxml","MainStyle.css");
    }
}
