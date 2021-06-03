package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
//<?import View.*?>
public class MyViewController extends AView implements Observer {

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
        stage.widthProperty().addListener(event -> drawMaze());
        stage.heightProperty().addListener(event -> drawMaze());
    }

    @Override
    public void setMyViewModel(MyViewModel myViewModel) {
        super.setMyViewModel(myViewModel);
        this.myViewModel.addObserver(this);
    }

    public void generateMaze(ActionEvent actionEvent) {
        int rows =Integer.valueOf(textField_mazeRows.getText());
        int cols =Integer.valueOf(textField_mazeColumns.getText());

        myViewModel.generateMaze(rows,cols);

        buttonSolveMaze.setDisable(false);
        buttonSolveMaze.setSelected(false);
        buttonHint.setDisable(false);
        buttonSave.setDisable(false);
    }

    public void solveMaze(ActionEvent actionEvent) {
        boolean state = buttonSolveMaze.selectedProperty().getValue();
        if(state)
            myViewModel.solveMaze();
        else
            drawMaze();
    }

    public void keyPressed(KeyEvent keyEvent){
        myViewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }

    public void getHint(ActionEvent actionEvent) {
        myViewModel.setHint();
        buttonSolveMaze.setSelected(false);
    }

    @Override
    public void setChosenChar(String chosenChar) {
        String filePath = "./resources/images/characters_goals/"+chosenChar+".png";
        mazeDisplayer.setImageFileNamePlayer(filePath);
        super.setChosenChar(chosenChar);
    }

    @Override
    public void exit() {
        myViewModel.exit();
        super.exit();
    }

    private void drawMaze(){
        mazeDisplayer.setWidth(stage.getWidth()/1.5);
        mazeDisplayer.setHeight(stage.getHeight()/1.5);
        mazeDisplayer.drawMaze(maze);
        if(buttonSolveMaze.selectedProperty().getValue())
            mazeDisplayer.drawSolution(myViewModel.getSolution());

    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel) {
            String change = (String)arg;
            switch (change) {
                case "maze generated"-> mazeGenerated();
                case "player moved" -> playerMoved();
                case "hint generated" -> hintGenerated();
                case "maze solved" -> mazeSolved();
                case "goal reached" ->goalReached();
            }
        }
    }

    private void mazeGenerated() {
        this.maze = myViewModel.getMaze();
        drawMaze();
        this.mazeDisplayer.setGoal(myViewModel.getGoalRow(), myViewModel.getGoalCol());
        playerMoved();

    }

    private void mazeSolved() {
        int[][] solution = myViewModel.getSolution();
        mazeDisplayer.drawSolution(solution);

    }

    private void hintGenerated() {
        int[] hint = myViewModel.getHint();
        mazeDisplayer.drawHint(hint);
    }

    private void playerMoved() {
        int row =myViewModel.getPlayerRow();
        int col =myViewModel.getPlayerCol();
        mazeDisplayer.setPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
        if(buttonSolveMaze.selectedProperty().getValue())
            myViewModel.solveMaze();
    }

    private void goalReached() {
        //play winning music
        //information about winning
    }

    public void zoomin(ZoomEvent zoomEvent) {
        System.out.println("scroll");
    }
}

