package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observer;

public interface IModel {
    int[][] getMaze();
    int[][] getSolution();
    int[] getHint();
    int getPlayerRow();
    int getPlayerCol();
    int getGoalRow();
    int getGoalCol();
    void generateMaze(int row, int col);
    void solveMaze();
    void setHint();
    void updatePlayerPosition(MovementDirection direction);
    void exit();
    void saveMaze(File file);
    void loadMaze(File file);
    void assignObserver(Observer o);
}
