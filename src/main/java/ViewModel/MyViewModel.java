package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    //View Model connects between View and Model layers. and verifies validity fof input and output
    private IModel model;

    public MyViewModel(IModel model) {
        //constructor of VM, assigning the model to be observed by myViewModel
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        //in case of changes in Model, we pass the changes to the View section
        if(o instanceof IModel)
        {
            setChanged();
            notifyObservers(arg);
        }
    }

    public int[][] getMaze() {return model.getMaze(); }

    public int[][] getSolution() {return model.getSolution(); }

    public int[] getHint() {return model.getHint();}

    public int getPlayerRow() {return model.getPlayerRow(); }

    public int getPlayerCol() {return model.getPlayerCol(); }

    public int getGoalRow() {return model.getGoalRow(); }

    public int getGoalCol() {return model.getGoalCol(); }

    public void generateMaze(int row,int col)
    {
        model.generateMaze(row,col);
    }

    public void solveMaze()
    {
        model.solveMaze();
    }

    public void setHint() {model.setHint(); }

    public void movePlayer(KeyEvent keyEvent){
        //receives a key event from myViewModel, check which key was pressed,
        //translate it to a direction and pass it to Model
        MovementDirection direction;

        switch (keyEvent.getCode()){
            case NUMPAD1,DIGIT1 -> direction = MovementDirection.DOWNLEFT;
            case NUMPAD2,DIGIT2,DOWN -> direction = MovementDirection.DOWN;
            case NUMPAD3,DIGIT3 -> direction = MovementDirection.DOWNRIGHT;
            case NUMPAD4,DIGIT4,LEFT -> direction =MovementDirection.LEFT;
            case NUMPAD6,DIGIT6,RIGHT -> direction = MovementDirection.RIGHT;
            case NUMPAD7,DIGIT7 -> direction = MovementDirection.UPLEFT;
            case NUMPAD8,DIGIT8,UP -> direction = MovementDirection.UP;
            case NUMPAD9,DIGIT9 -> direction = MovementDirection.UPRIGHT;
            default -> {return;}

        }

        model.updatePlayerPosition(direction);
    }

    public void movePlayerMouse(int newRow,int newCol,int oldRow, int oldCol){
        //receives rows an columns from before and after drag, computes the diff between rows and cols,
        //translate it to a direction and pass it to Model
        MovementDirection direction;
        int deltaRow= newRow-oldRow;
        int deltaCol= newCol-oldCol;
        if(deltaRow>0){
            if(deltaCol<0)
                direction = MovementDirection.DOWNLEFT;
            else if(deltaCol==0)
                direction = MovementDirection.DOWN;
            else
                direction = MovementDirection.DOWNRIGHT;
        }
        else if(deltaRow==0){
            if(deltaCol<0)
                direction = MovementDirection.LEFT;
            else if(deltaCol>0)
                direction = MovementDirection.RIGHT;
            else
                return;
        }
        else{
            if(deltaCol<0)
                direction = MovementDirection.UPLEFT;
            else if(deltaCol==0)
                direction = MovementDirection.UP;
            else
                direction = MovementDirection.UPRIGHT;
        }
        model.updatePlayerPosition(direction);
    }

    public void exit()
    {
        model.exit();
    }

    public void saveMaze(File savedFile) {
        model.saveMaze( savedFile);
    }

    public void loadMaze(File file){
        model.loadMaze(file);
    }

}
