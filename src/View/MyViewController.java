package View;

import ViewModel.MyViewModel;
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
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
//<?import View.*?>
public class MyViewController extends AView implements Observer {

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

    public void setUpdatePlayerRow(int row) { this.updatePlayerRow.set("" + row); }
    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_PlayerRow.textProperty().bind(updatePlayerRow);
        lbl_PlayerCol.textProperty().bind(updatePlayerCol);
        stage.widthProperty().addListener(event -> {
            mazeDisplayer.widthProperty().setValue(stage.widthProperty().getValue()/1.5);
            mazeDisplayer.drawMaze(maze);
        });
        stage.heightProperty().addListener(event -> {
            mazeDisplayer.heightProperty().setValue(stage.heightProperty().getValue()/(1.5));
            mazeDisplayer.drawMaze(maze);
        });
    }



    public void generateMaze(ActionEvent actionEvent) {
        //if(generator==null )
          //  generator = new MazeGenerator();
        int rows =Integer.valueOf(textField_mazeRows.getText());
        int cols =Integer.valueOf(textField_mazeColumns.getText());

        //maze = generator.generateRandomMaze(rows,cols);

        myViewModel.generateMaze(rows,cols);

        buttonSolveMaze.setDisable(false);
        buttonSolveMaze.setSelected(false);
        buttonHint.setDisable(false);
        buttonSave.setDisable(false);
    }

    public void solveMaze(ActionEvent actionEvent) {
        if(buttonSolveMaze.selectedProperty().getValue())
            myViewModel.solveMaze();
        else
            mazeDisplayer.drawMaze(maze);
        buttonSolveMaze.setSelected(!buttonSolveMaze.selectedProperty().getValue());
    }

    public void keyPressed(KeyEvent keyEvent){

        int direction = -1;

        switch (keyEvent.getCode()){
            case NUMPAD1,DIGIT1 -> direction = 1;
            case NUMPAD2,DIGIT2,DOWN -> direction = 2;
            case NUMPAD3,DIGIT3 -> direction = 3;
            case NUMPAD4,DIGIT4,LEFT -> direction = 4;
            case NUMPAD6,DIGIT6,RIGHT -> direction = 6;
            case NUMPAD7,DIGIT7 -> direction = 7;
            case NUMPAD8,DIGIT8,UP -> direction = 8;
            case NUMPAD9,DIGIT9 -> direction = 9;

        }
        myViewModel.updatePlayerPosition(direction);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }

    public void getHint(ActionEvent actionEvent) {

        myViewModel.setHint();
        buttonSolveMaze.setSelected(false);
    }

    //myTest:
    public void setCharacter(String charName){
        String filePath = "./resources/images/characters_goals/"+charName+".png";
        mazeDisplayer.setImageFileNamePlayer(filePath);
    }

    @Override
    public void exit() {
        myViewModel.exit();
        super.exit();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel) {
            switch ((int) arg) {
                case 0: //generateMaze
                    this.maze = myViewModel.getMaze();
                    mazeDisplayer.drawMaze(maze);
                    this.mazeDisplayer.setGoal(myViewModel.getGoalRow(), myViewModel.getGoalCol());
                    setUpdatePlayerRow(myViewModel.getPlayerRow());
                    setUpdatePlayerCol(myViewModel.getPlayerCol());
                    this.mazeDisplayer.setPosition(myViewModel.getPlayerRow(), myViewModel.getPlayerCol());

                    break;
                case 1: //solveMaze
                    int[][] solution = myViewModel.getSolution();
                    mazeDisplayer.drawSolution(solution);
                    break;
                case 2: //getHint
                    int[] hint = myViewModel.getHint();
                    mazeDisplayer.drawHint(hint);
                    break;
                case 3: //updatePlayerPosition
                    int row =myViewModel.getPlayerRow();
                    int col =myViewModel.getPlayerCol();
                    mazeDisplayer.setPosition(row,col);
                    setUpdatePlayerRow(row);
                    setUpdatePlayerCol(col);
                    if(buttonSolveMaze.selectedProperty().getValue())
                        myViewModel.solveMaze();
                    break;
            }
        }

    }
}

