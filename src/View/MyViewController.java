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
public class MyViewController extends AView {
    public MazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label lbl_PlayerRow;
    public Label lbl_PlayerCol;
    public Button buttonHint;
    public RadioButton buttonSolveMaze;
    public MenuItem buttonSave;
    private int [][] maze;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

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
        buttonSave.setDisable(false);
    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("solving the maze...");
        alert.show();
    }

    public void keyPressed(KeyEvent keyEvent){
        //need to pass which key to the modelView
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

    public void getHint(ActionEvent actionEvent) {

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

