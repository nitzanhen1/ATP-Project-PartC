package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    StringProperty imageFileNameWall= new SimpleStringProperty();
    StringProperty imageFileNamePlayer= new SimpleStringProperty();
    StringProperty imageFileNameGoal= new SimpleStringProperty();
    private int PlayerRow=0;
    private int PlayerCol=0;
    private int GoalRow=0;
    private int GoalCol=0;
    private int rows;
    private int cols;
    private double cellHeight;
    private double cellWidth;
    private GraphicsContext graphicsContext;

    public int getPlayerRow() {
        return PlayerRow;
    }

    public int getPlayerCol() {
        return PlayerCol;
    }

    public void setPosition(int row, int col){
        PlayerRow=row;
        PlayerCol=col;
        draw();
    }

    public int getGoalRow() {
        return GoalRow;
    }

    public int getGoalCol() {
        return GoalCol;
    }

    public void setGoal(int row, int col){
        GoalRow=row;
        GoalCol=col;
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {this.imageFileNamePlayer.set(imageFileNamePlayer);}

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public void drawMaze(int[][] maze) {
        if(maze==null) {
            return;
        }
        this.maze = maze;
        draw();
    }

    private void draw() {
        if(maze != null){
            setCanvas();
            //draw each component of mazeDisplay
            drawMazeWalls(graphicsContext,rows,cols, cellHeight,cellWidth);
            drawGoal(graphicsContext,cellHeight,cellWidth);
            drawPlayer(graphicsContext,cellHeight,cellWidth);
        }
    }

    private void setCanvas(){
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        rows = maze.length;
        cols = maze[0].length;
        cellHeight = canvasHeight/rows;
        cellWidth = canvasWidth/cols;
        graphicsContext =  getGraphicsContext2D();
        //clear the canvas
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {
        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j* cellWidth;
                double y = i* cellHeight;
                if(maze[i][j]==1){
                    //it is a wall
                    if(wallImage==null)
                        graphicsContext.fillRect(x,y,cellWidth,cellHeight);
                    else {
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image");
        }
        graphicsContext.setFill(Color.GREEN);

        double x = getPlayerCol()*cellWidth;
        double y = getPlayerRow()*cellHeight;
        if(playerImage==null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(playerImage,x,y,cellWidth,cellHeight);
    }

    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image goalImage = null;
        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image");
        }
        graphicsContext.setFill(Color.GREEN);

        double x = getGoalCol()*cellWidth;
        double y = getGoalRow()*cellHeight;
        if(goalImage==null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(goalImage,x,y,cellWidth,cellHeight);
    }

    public void drawSolution(int[][] solution) {
        setCanvas();
        draw();
        graphicsContext.setFill(Color.TURQUOISE);
        for(int i=1; i<solution.length-1; i++){
            double x = solution[i][1]* cellWidth;
            double y = solution[i][0]* cellHeight;
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        }
    }

    public void drawHint(int[] hint) {
        setCanvas();
        draw();
        graphicsContext.setFill(Color.PINK);
        double x = hint[1]* cellWidth;
        double y = hint[0]* cellHeight;
        graphicsContext.fillRect(x,y,cellWidth,cellHeight);
    }
}
