package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

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
        generateMazeServer = new Server(5400,1000,new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401,1000,new ServerStrategySolveSearchProblem());
        generateMazeServer.start();
        solveMazeServer.start();
    }

    public int[][] getMaze() {return maze.getMaze(); }

    public int[][] getSolution() {
        ArrayList<AState> sol = solution.getSolutionPath();
        int[][] solutionAsArray = new int[sol.size()][2];
        for (int i = 0; i < sol.size(); i++) {
            solutionAsArray[i][0] = ((MazeState) sol.get(i)).getRowIndex();
            solutionAsArray[i][1] = ((MazeState) sol.get(i)).getColumnIndex();
        }
        return solutionAsArray;
    }

    public int[] getHint() {
        int[] hint = new int[2];
        hint[0] = ((MazeState) this.hint).getRowIndex();
        hint[1] = ((MazeState) this.hint).getColumnIndex();

        return hint;
    }

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
        notifyObservers("maze generated");
    }

    public void solveMaze() {
        updateSolution();
        setChanged();
        notifyObservers("maze solved");
    }

    public void setHint(){
        updateSolution();
        hint=solution.getSolutionPath().get(1);
        setChanged();
        notifyObservers("hint generated");
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

    public void updatePlayerPosition(MovementDirection direction){

        int tempRow=playerRow;
        int tempCol=playerCol;
        switch (direction)
        {
            case DOWNLEFT -> {
                if (maze.getCell(playerRow, playerCol - 1) == 0 || maze.getCell(playerRow + 1, playerCol) == 0) {
                    tempRow++;
                    tempCol--;
                }
            }
            case DOWN -> tempRow++;
            case DOWNRIGHT -> {
                if (maze.getCell(playerRow, playerCol + 1) == 0 || maze.getCell(playerRow + 1, playerCol) == 0) {
                    tempRow++;
                    tempCol++;
                }
            }
            case LEFT -> tempCol--;
            case RIGHT -> tempCol++;
            case UPLEFT -> {
                if (maze.getCell(playerRow, playerCol - 1) == 0 || maze.getCell(playerRow - 1, playerCol) == 0) {
                    tempRow--;
                    tempCol--;
                }
            }
            case UP -> tempRow--;
            case UPRIGHT -> {
                if (maze.getCell(playerRow, playerCol + 1) == 0 || maze.getCell(playerRow - 1, playerCol) == 0) {
                    tempRow--;
                    tempCol++;
                }
            }
        }

        if(maze.getCell(tempRow,tempCol)==0){
            playerRow=tempRow;
            playerCol=tempCol;
        }
        setChanged();
        notifyObservers("player moved");
        if(playerRow==goalRow && playerCol==goalCol)
            notifyObservers("goal reached");
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
            notifyObservers("maze generated");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }
}
