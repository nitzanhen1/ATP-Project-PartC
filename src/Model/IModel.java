package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface IModel {
    int[][] getMaze();
    Solution getSolution();
    AState getHint();
    int getPlayerRow();
    int getPlayerCol();
    int getGoalRow();
    int getGoalCol();
    void generateMaze(int row, int col);
    void solveMaze();
    void setHint();
    void updatePlayerPosition(int direction);
    void exit();
    void saveMaze(File file);
    void loadMaze(File file);
}
