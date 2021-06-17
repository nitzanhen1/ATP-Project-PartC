package View;
import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
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
    private boolean dragOnPlayer=false;

    public void setUpdatePlayerRow(int row) { this.updatePlayerRow.set("" + row); }
    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //binding of properties of player row and col
        lbl_PlayerRow.textProperty().bind(updatePlayerRow);
        lbl_PlayerCol.textProperty().bind(updatePlayerCol);
        //binding of the properties of stage size to maze size
        //this will make the maze strech in/out when stage sizes are changed
        stage.widthProperty().addListener(event -> drawMaze());
        stage.heightProperty().addListener(event -> drawMaze());
        //change the music to mazeSong and check if user already muted it
        setMusic(mazeSong);
        if(!musicState)
            muteButton.setSelected(true);
    }

    @Override
    public void setMyViewModel(MyViewModel myViewModel) {
        //overrides AView function and add myViewModel as an observer
        super.setMyViewModel(myViewModel);
        this.myViewModel.addObserver(this);
    }

    public void generateMaze(ActionEvent actionEvent) {
        //event handler for the button 'generate Maze'
        //gets row and col sizes from the textField and calls VM to generate the maze
        try {
            int rows = Integer.valueOf(textField_mazeRows.getText());
            int cols = Integer.valueOf(textField_mazeColumns.getText());
            myViewModel.generateMaze(rows,cols);
        }

        //VM and Model verifies the sizes for the maze and throw exceptions if needed.
        //the exceptions are cached and a Warning Alert is shown
        catch (NumberFormatException e){
            showAlert(Alert.AlertType.WARNING,"Illegal input", "must insert positive numbers for maze row and column sizes");
        }
        catch (IllegalArgumentException e){
            showAlert(Alert.AlertType.WARNING,"Illegal input", "min size of maze is 2X2");
        }
        //to make it more easy for the user, after the maze is generated they can start without pressing on the mazeDisplayer's pane
        mazeDisplayer.requestFocus();
    }

    public void solveMaze(ActionEvent actionEvent) {
        //event handler for the radio button 'solve Maze'
        //if the button is not pressed, we call VM to solve the maze,
        //else, we draw the maze again to hide the solution
        boolean state = buttonSolveMaze.selectedProperty().getValue();
        if(state)
            myViewModel.solveMaze();
        else
            drawMaze();

        //to make it more easy for the user, after the maze is solved they can start without pressing on the mazeDisplayer's pane
        mazeDisplayer.requestFocus();
    }

    public void getHint(ActionEvent actionEvent) {
        //event handler for the button 'Hint'
        //calls VM to generate a hint
        myViewModel.setHint();
        buttonSolveMaze.setSelected(false);
        //to make it more easy for the user, after the hind is generated they can start without pressing on the mazeDisplayer's pane
        mazeDisplayer.requestFocus();
    }

    private void drawMaze(){
        //this func is bounded to the size properties of the stages and changes the  sizes of the maze if neeeded
        //this func is also called from 'mazeGenerated' func and when solve is hidden
        mazeDisplayer.setWidth(stage.getWidth()/1.5);
        mazeDisplayer.setHeight(stage.getHeight()/1.5);
        mazeDisplayer.drawMaze(maze);
        if(buttonSolveMaze.selectedProperty().getValue())
            mazeDisplayer.drawSolution(myViewModel.getSolution());
    }

    private void setPlayerPosition(int row, int col){
        //this func is called from 'playerMoved func to update the players location in maze displayer and the string properties of rows and cols
        mazeDisplayer.setPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    @Override
    public void setChosenChar(String chosenChar) {
        //when character is chosen, myViewController changes the image of the player for mazeDisplayer
        String filePath = "./resources/images/characters_goals/"+chosenChar+".png";
        mazeDisplayer.setImageFileNamePlayer(filePath);
        super.setChosenChar(chosenChar);
    }

    @Override
    public void exit() {
        //properly exit the program via closing myViewModel (which closes the servers)
        myViewModel.exit();
        super.exit();
    }

    public void keyPressed(KeyEvent keyEvent){
        //event handler for keyBoard input (when mazeDisplayer is in focus)
        //calls VM to check direction
        if(maze== null)
            return;
        myViewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent){
        //when mouse is clicked in the area of mazeDisplayer then mazeDisplayer is in focus and can accept other events
        mazeDisplayer.requestFocus();
    }

    public void zoom(ScrollEvent scrollEvent) {
        //event handler for zoom events, this event check is the maze exists and if ctrl is pressed
        //we change the sizes of mazeDisplayer (increase or decease) depending on the direction of the mouse scroll
        if(maze==null)
            return;
        if(scrollEvent.isControlDown()) {
            double zoomFactor =1.05;
            double mazeWidth=mazeDisplayer.getWidth();
            double mazeHeight=mazeDisplayer.getHeight();
            if(scrollEvent.getDeltaY()<0){
                zoomFactor=0.95;
            }
            if(mazeWidth*mazeHeight>7000000 && zoomFactor==1.05)
                return;

            //change mazeDisplayer sizes and draw the maze with the new size
            mazeDisplayer.setWidth(mazeWidth* zoomFactor);
            mazeDisplayer.setHeight(mazeHeight * zoomFactor);
            mazeDisplayer.drawMaze(maze);
            if(buttonSolveMaze.selectedProperty().getValue())//if solve maze button is turned on, we draw the solution again
                mazeDisplayer.drawSolution(myViewModel.getSolution());
        }
        scrollEvent.consume();
    }

    @Override
    public void update(Observable o, Object arg) {
        //when VM is changed we update myController with the specific change
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
        //model has created a new Maze, we receive it and set the newMaze and goal positions
        showButtons();
        this.maze = myViewModel.getMaze();
        drawMaze();
        this.mazeDisplayer.setGoal(myViewModel.getGoalRow(), myViewModel.getGoalCol());
        playerMoved();

    }

    private void mazeSolved() {
        //Model has created a solution for the maze, we receive it and draw the solution with mazeDisplayer
        int[][] solution = myViewModel.getSolution();
        mazeDisplayer.drawSolution(solution);
    }

    private void hintGenerated() {
        //Model has created a hint for the maze, we receive it and draw it with mazeDisplayer
        int[] hint = myViewModel.getHint();
        mazeDisplayer.drawHint(hint);
    }

    private void playerMoved() {
        //Model has moved the player (based on mouse/keyborad event), we update the player position
        //this calls drawMaze again with the new location of the player
        setPlayerPosition(myViewModel.getPlayerRow(),myViewModel.getPlayerCol());
        //if solve button is pressed we draw the solution again
        if(buttonSolveMaze.selectedProperty().getValue())
            myViewModel.solveMaze();
    }


    private void goalReached() {
        //playing winning music
        //information about winning
        MediaPlayer winPlayer = new MediaPlayer(winSong);
        winPlayer.setAutoPlay(true);
        winPlayer.setVolume(0.4);
        winPlayer.setStopTime(new Duration(3000));

        Alert alert = showAlert(Alert.AlertType.INFORMATION,"winner!!!","Congratulations, you won!\nfriends have reached the couch!!");
        alert.setOnCloseRequest(e->winPlayer.pause());
    }

    public void startDrag(MouseEvent e){
        //if maze is not null, verify that the dragging start from the cel of the player
        if(maze==null)
            return;
        double mouseCol = e.getX();
        double mouseRow = e.getY();
        int playerRow = (int)(mouseRow/mazeDisplayer.getCellHeight());
        int playerCol = (int)(mouseCol/mazeDisplayer.getCellWidth());
        if(mazeDisplayer.getPlayerRow()==playerRow&&mazeDisplayer.getPlayerCol()==playerCol)
            dragOnPlayer=true;
    }
    public void dragCharacter(MouseEvent e){
        //if maze is not null, compute the movemenet of the mouse and determine of the drag shoud move the player to another cell
        if(maze==null)
            return;
        if(!dragOnPlayer)
            return;
        double mouseCol =  e.getX();
        double mouseRow = e.getY();
        int newPlayerRow = (int)(mouseRow/mazeDisplayer.getCellHeight());
        int newPlayerCol = (int)(mouseCol/mazeDisplayer.getCellWidth());
        int diffRow=Math.abs(mazeDisplayer.getPlayerRow()-newPlayerRow);
        int diffCol = Math.abs(mazeDisplayer.getPlayerCol()-newPlayerCol);
        //if the drag was just one cell movement, call VM to move the player
        if(diffRow<=1&&diffCol<=1&&(diffRow!=0||diffCol!=0)){
            myViewModel.movePlayerMouse(newPlayerRow,newPlayerCol,mazeDisplayer.getPlayerRow(),mazeDisplayer.getPlayerCol());
        }

        e.consume();
    }

    public void stopDrag(MouseEvent mouseEvent) {
        //if the maze is not null, when dragging event stops we change the flag back to false for the next drag event
        if(maze==null)
            return;
        dragOnPlayer=false;
    }



    public void showButtons() {
        //after generation first maze, enable solve, hint and save buttons
        buttonSolveMaze.setSelected(false);
        buttonSolveMaze.setDisable(false);
        buttonSave.setDisable(false);
        buttonHint.setDisable(false);
    }
}

