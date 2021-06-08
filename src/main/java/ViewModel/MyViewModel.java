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
    private IModel model;

    public MyViewModel(IModel model) {

        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
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

    public void movePlayerMouse(int row,int col){
        model.updatePlayerPositionMouse(row,col);
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
