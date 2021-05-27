package View;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//<?import View.*?>
public class MyViewController implements Initializable, IView {
    public MazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label lbl_PlayerRow;
    public Label lbl_PlayerCol;
    public static Stage stage;
    public Button buttonHint;
    public RadioButton buttonSolveMaze;
    public MenuItem muteUnmuteButton;
    public Button instruction;
    private int [][] maze;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }
    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }
    public void setUpdatePlayerRow(int row) {
        this.updatePlayerRow.set("" + row);
    }
    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_PlayerRow.textProperty().bind(updatePlayerRow);
        lbl_PlayerCol.textProperty().bind(updatePlayerCol);

    }

    public void generateMaze(ActionEvent actionEvent) {
        if(generator==null )
            generator = new MazeGenerator();
        int rows =Integer.valueOf(textField_mazeRows.getText());
        int cols =Integer.valueOf(textField_mazeColumns.getText());
        maze = generator.generateRandomMaze(rows,cols);

        stage.widthProperty().addListener(event -> {
            mazeDisplayer.widthProperty().setValue(stage.widthProperty().getValue()/1.5);
            mazeDisplayer.drawMaze(maze);
            //mazeDisplayer.drawPlayerAndGoal();
        });
        stage.heightProperty().addListener(event -> {
            mazeDisplayer.heightProperty().setValue(stage.heightProperty().getValue()/(1.5));
            mazeDisplayer.drawMaze(maze);
            //mazeDisplayer.drawPlayerAndGoal();
        });

        mazeDisplayer.drawMaze(maze);
        buttonSolveMaze.setDisable(false);
        buttonHint.setDisable(false);
    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("solving the maze...");
        alert.show();
    }

    public void keyPressed(KeyEvent keyEvent){
        int row = mazeDisplayer.getPlayerRow();
        int col = mazeDisplayer.getPlayerCol();
        //need to check bounderies but not here!! where is next week lab

        switch (keyEvent.getCode()) {
            case UP -> row -= 1;
            case DOWN -> row += 1;
            case LEFT -> col -= 1;
            case RIGHT -> col += 1;
        }
        mazeDisplayer.setPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }


    public void setStage(Stage Stage) {
        this.stage = Stage;
    }

    public void getHint(ActionEvent actionEvent) {

    }

    public void muteUnmute(ActionEvent actionEvent) {

    }

    public void saveMaze(ActionEvent actionEvent) {
    }

    public void loadMaze(ActionEvent actionEvent) {
    }

    public void Properties(ActionEvent actionEvent) {
    }

    public void MuteUnmute(ActionEvent actionEvent) {
    }

    public void exitButton(ActionEvent actionEvent) {
    }

    public void chooseCharacter(ActionEvent actionEvent) {
    }

    public void about(ActionEvent actionEvent) {
    }

    public void help(ActionEvent actionEvent) {
        try {
            //Stage newStage = new Stage();
            //newStage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 900, 900);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            //newStage.setScene(scene);
            //newStage.show();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {
    }

    public void properties(ActionEvent actionEvent) {
    }

    public void play(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("MyStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backToMain(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MainView.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

