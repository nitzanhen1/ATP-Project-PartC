package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class MyModel extends Observable implements IModel{

    private Server generateMazeServer;
    private Server solveMazeServer;
    private Maze maze;
    private Solution solution;
    private AState hint;
    private int playerRow;
    private int playerCol;
    private int goalRow;
    private int goalCol;

    public MyModel() {
        /*maze = null;
        playerRow =0;
        playerRow =0;
        goalRow=0;
        goalCol=0;*/
        generateMazeServer = new Server(5400,1000,new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401,1000,new ServerStrategySolveSearchProblem());
        generateMazeServer.start();
        solveMazeServer.start();
    }

    public int[][] getMaze() {return maze.getMaze(); }

    public Solution getSolution() {return solution; }

    public AState getHint() {return hint; }

    public int getPlayerRow() {return playerRow; }

    public int getPlayerCol() {return playerCol; }

    public int getGoalRow() {return goalRow; }

    public int getGoalCol() {return goalCol; }

    public void generateMaze(int row, int col){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row,col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[row*col +12 /*CHANGESIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressedmaze -

                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        playerRow=maze.getStartPosition().getRowIndex();
        playerCol=maze.getStartPosition().getColumnIndex();
        goalRow=maze.getGoalPosition().getRowIndex();
        goalCol=maze.getGoalPosition().getColumnIndex();
        setChanged();
        notifyObservers(0);
    }

    public void solveMaze() {
        updateSolution();
        setChanged();
        notifyObservers(1);
    }

    public void setHint(){
        updateSolution();
        hint=solution.getSolutionPath().get(1);
        setChanged();
        notifyObservers(2);
    }

    private void updateSolution(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer,OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        maze.setStartPosition(playerRow,playerCol);
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerPosition(int direction){

        int tempRow=playerRow;
        int tempCol=playerCol;
        switch (direction)
        {
            case 1: //Down + Left
                if(maze.getCell(playerRow,playerCol-1)==0 ||maze.getCell(playerRow+1,playerCol)==0 ) {
                    tempRow++;
                    tempCol--;
                }
                break;
            case 2: //Down
                tempRow++;
                break;
            case 3: // Down + Right
                if(maze.getCell(playerRow,playerCol+1)==0 ||maze.getCell(playerRow+1,playerCol)==0 ) {
                    tempRow++;
                    tempCol++;
                }
                break;
            case 4: // Left
                tempCol--;
                break;
            case 6: //Right
                tempCol++;
                break;
            case 7: //Up + Left
                if(maze.getCell(playerRow,playerCol-1)==0 ||maze.getCell(playerRow-1,playerCol)==0 ) {
                    tempRow--;
                    tempCol--;
                }
                break;
            case 8: //Up
                tempRow--;
                break;
            case 9: // Up + Right
                if(maze.getCell(playerRow,playerCol+1)==0 ||maze.getCell(playerRow-1,playerCol)==0 ) {
                    tempRow--;
                    tempCol++;
                }
                break;
        }

        if(maze.getCell(tempRow,tempCol)==0){
            playerRow=tempRow;
            playerCol=tempCol;
        }
        setChanged();
        notifyObservers(3);
    }

    public void exit(){
        stopServers();
    }
    private void stopServers(){
        generateMazeServer.stop();
        solveMazeServer.stop();
    }

    public void saveMaze(File file) {
        /**
         * Saving the maze into the path the user choose.
         */
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(file));
            maze.setStartPosition(playerRow,playerCol);
            objectOutput.writeObject(maze.toByteArray());
            objectOutput.flush();
            objectOutput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMaze(File file) {
        /**
         * load maze from a path that the user choose
         */
        try {
            ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(file));
            byte[] loadedMaze = (byte[]) objectIn.readObject();
            objectIn.close();
            maze = new Maze(loadedMaze);
            playerRow = maze.getStartPosition().getRowIndex();
            playerCol = maze.getStartPosition().getColumnIndex();
            goalRow = maze.getGoalPosition().getRowIndex();
            goalCol = maze.getGoalPosition().getColumnIndex();
            setChanged();
            notifyObservers(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
