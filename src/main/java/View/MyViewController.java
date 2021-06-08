package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    public RadioButton muteButton;
    private int [][] maze;

    Media mazeSong = new Media(getClass().getResource("../music/mazeSong.mp3").toExternalForm());
    Media winSong = new Media(getClass().getResource("../music/winSong.mp3").toExternalForm());

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    private boolean flagPlayer=false;
    private double zoom=1;

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
        setMusic(mazeSong);
        if(!musicState)
            muteButton.setSelected(true);
    }

    @Override
    public void setMyViewModel(MyViewModel myViewModel) {
        super.setMyViewModel(myViewModel);
        this.myViewModel.addObserver(this);
    }

    public void generateMaze(ActionEvent actionEvent) {
        try {
            int rows = Integer.valueOf(textField_mazeRows.getText());
            int cols = Integer.valueOf(textField_mazeColumns.getText());
            myViewModel.generateMaze(rows,cols);
            zoom=1;
        }
        catch (NumberFormatException e){
            showAlert(Alert.AlertType.WARNING,"Illegal input", "must insert positive numbers for maze row and column sizes");
        }
        catch (IllegalArgumentException e){
            showAlert(Alert.AlertType.WARNING,"Illegal input", "min size of maze is 2X2");
        }

        mazeDisplayer.requestFocus();
    }

    public void solveMaze(ActionEvent actionEvent) {
        boolean state = buttonSolveMaze.selectedProperty().getValue();
        if(state)
            myViewModel.solveMaze();
        else
            drawMaze();
        mazeDisplayer.requestFocus();
    }

    public void getHint(ActionEvent actionEvent) {
        myViewModel.setHint();
        buttonSolveMaze.setSelected(false);
        mazeDisplayer.requestFocus();
    }

    private void drawMaze(){
        mazeDisplayer.setWidth(zoom*stage.getWidth()/1.5);
        mazeDisplayer.setHeight(zoom*stage.getHeight()/1.5);
        mazeDisplayer.drawMaze(maze);
        if(buttonSolveMaze.selectedProperty().getValue())
            mazeDisplayer.drawSolution(myViewModel.getSolution());
    }

    private void setPlayerPosition(int row, int col){
        mazeDisplayer.setPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
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

    public void keyPressed(KeyEvent keyEvent){
        myViewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }

    public void zoom(ScrollEvent scrollEvent) {
        if(maze==null)
            return;
        if(scrollEvent.isControlDown()) {
            double zoomFactor =1.05;
            double mazeWidth=mazeDisplayer.getWidth();
            double mazeHeight=mazeDisplayer.getHeight();
            if(scrollEvent.getDeltaY()<0){
                zoomFactor=0.95;
            }
            System.out.println(zoom);
            if(mazeWidth*mazeHeight>5000000 && zoomFactor==1.05)
                return;
            zoom*=zoomFactor;
            mazeDisplayer.setWidth(mazeWidth* zoomFactor);
            mazeDisplayer.setHeight(mazeHeight * zoomFactor);
            System.out.println(mazeDisplayer.getWidth());
            System.out.println(mazeDisplayer.getHeight());
            mazeDisplayer.drawMaze(maze);
            if(buttonSolveMaze.selectedProperty().getValue())
                mazeDisplayer.drawSolution(myViewModel.getSolution());

        }
        scrollEvent.consume();
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


    private void goalReached() {
        //play winning music
        //information about winning
        setMusic(winSong);
        showAlert(Alert.AlertType.INFORMATION,"winner!!!","Congratulations, you won!\n"+getChosenChar()+" has reached the couch!!");
    }

    public void startDrag(MouseEvent e){
        double mouseCol = e.getX();
        double mouseRow = e.getY();
        int playerRow = (int)(mouseRow/mazeDisplayer.getCellHeight());
        int playerCol = (int)(mouseCol/mazeDisplayer.getCellWidth());
        if(mazeDisplayer.getPlayerRow()==playerRow&&mazeDisplayer.getPlayerCol()==playerCol)
            flagPlayer=true;
    }
    public void dragCharacter(MouseEvent e){
        if(!flagPlayer)
            return;
        double mouseCol =  e.getX();
        double mouseRow = e.getY();
        int playerRow = (int)(mouseRow/mazeDisplayer.getCellHeight());
        int playerCol = (int)(mouseCol/mazeDisplayer.getCellWidth());
        int diffRow=Math.abs(mazeDisplayer.getPlayerRow()-playerRow);
        int diffCol = Math.abs(mazeDisplayer.getPlayerCol()-playerCol);
        if(diffRow<=1&&diffCol<=1&&(diffRow!=0||diffCol!=0)){
            myViewModel.movePlayerMouse(playerRow,playerCol);
        }

        e.consume();
    }

    public void stopDrag(MouseEvent mouseEvent) {
        flagPlayer=false;
    }



    public void showButtons() {
        buttonSolveMaze.setSelected(false);
        buttonSolveMaze.setDisable(false);
        buttonSave.setDisable(false);
        buttonHint.setDisable(false);
    }
}

