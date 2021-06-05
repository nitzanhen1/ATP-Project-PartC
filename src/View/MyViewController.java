package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;

import java.io.File;
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
    public ScrollPane scrollPane;
    private int [][] maze;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    private boolean flagPlayer=false;

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
        //showButtons();
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
        showButtons();
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
        setPlayerPosition(myViewModel.getPlayerRow(),myViewModel.getPlayerCol());

        if(buttonSolveMaze.selectedProperty().getValue())
            myViewModel.solveMaze();
    }
    private void setPlayerPosition(int row, int col){
        mazeDisplayer.setPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    private void goalReached() {
        //play winning music
        //information about winning
    }

    public void zoomIn(ZoomEvent zoomEvent) {
        double zoomFactor =zoomEvent.getZoomFactor();
        mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*zoomFactor);
        mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*zoomFactor);
        zoomEvent.consume();
    }

    public void updateCoordinates(MouseEvent mouseEvent) {

        //myViewModel.MouseMove()
        double mouseCol = mouseEvent.getX();
        double mouseRow = mouseEvent.getY();
        double cellWidth = mazeDisplayer.getCellWidth();
        double cellHeight = mazeDisplayer.getCellHeight();
        double playerLeftBound = cellWidth * mazeDisplayer.getPlayerRow();
        double playerUpBound = cellHeight * mazeDisplayer.getPlayerCol();
        double playerRightBound = cellWidth + playerLeftBound;
        double playerDownBound = cellHeight + playerUpBound;
        if (playerLeftBound <= mouseCol && mouseCol <= playerRightBound && playerUpBound <= mouseRow && mouseRow <= playerDownBound) {
            flagPlayer = true;

        }
    }
    public void dragCharacter(MouseEvent mouseEvent) {
        if(!flagPlayer)
            return;
        System.out.println("true");
        double mouseCol =  mouseEvent.getX();
        double mouseRow = mouseEvent.getY();
        double cellWidth = mazeDisplayer.getCellWidth();
        double cellHeight = mazeDisplayer.getCellHeight();
        double playerLeftBound = cellWidth*mazeDisplayer.getPlayerRow();
        double playerUpBound = cellHeight*mazeDisplayer.getPlayerCol();
        double playerRightBound = cellWidth+playerLeftBound;
        double playerDownBound = cellHeight+playerUpBound;
        setPlayerPosition(mazeDisplayer.getPlayerRow() - 1, mazeDisplayer.getPlayerCol());
        if(mouseCol<playerLeftBound && mouseCol>= playerLeftBound-cellWidth){
            if(mouseRow<playerUpBound && mouseRow >= playerUpBound-cellHeight) {
                setPlayerPosition(mazeDisplayer.getPlayerRow() - 1, mazeDisplayer.getPlayerCol() - 1);
                System.out.println("UPLEFT");
            }
            else {
                setPlayerPosition(mazeDisplayer.getPlayerRow() - 1, mazeDisplayer.getPlayerCol());
                System.out.println("UP");
            }
        }
        mouseEvent.consume();

//        System.out.println("mouse: "+ mouseCol+" "+mouseRow);
//        System.out.println("point1: "+playerLeftBound+" "+playerUpBound);
//        System.out.println("point2: "+playerRightBound+" "+playerDownBound);

    }


    public void showButtons() {
        buttonSolveMaze.setSelected(false);
        buttonSolveMaze.setDisable(false);
        buttonSave.setDisable(false);
        buttonHint.setDisable(false);
    }
}

