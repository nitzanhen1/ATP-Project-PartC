package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
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

    public int[][] getSolution() {
        ArrayList<AState> sol = model.getSolution().getSolutionPath();
        int[][] solutionAsArray = new int[sol.size()][2];
        for (int i = 0; i < sol.size(); i++) {
            solutionAsArray[i][0] = ((MazeState) sol.get(i)).getRowIndex();
            solutionAsArray[i][1] = ((MazeState) sol.get(i)).getColumnIndex();
        }
        return solutionAsArray;
    }

    public int[] getHint() {
        int[] hint = new int[2];
        hint[0] = ((MazeState) model.getHint()).getRowIndex();
        hint[1] = ((MazeState) model.getHint()).getColumnIndex();
        return hint;
    }

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

    public void updatePlayerPosition(int direction){model.updatePlayerPosition(direction);}

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
