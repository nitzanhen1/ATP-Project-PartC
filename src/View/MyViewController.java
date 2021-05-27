package View;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

    }

    public void getHint(ActionEvent actionEvent) {

    }

    public void muteUnmute(ActionEvent actionEvent) {

    }
}

