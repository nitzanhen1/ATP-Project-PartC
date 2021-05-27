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
    StringProperty imageFileNameWall= new SimpleStringProperty();
    StringProperty imageFileNamePlayer= new SimpleStringProperty();
    private int PlayerRow=0;
    private int PlayerCol=0;


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

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }


    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    private int[][] maze;
    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
        setPosition(0,0);
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();

            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;
            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;
            GraphicsContext graphicsContext =  getGraphicsContext2D();
            //clear the canvas
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);

            drawMazeWalls(graphicsContext,rows,cols, cellHeight,cellWidth);
            drawPlayer(graphicsContext,cellHeight,cellWidth);

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

    private void drawMazeWalls(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {
        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image");
        }
       //graphicsContext.setFill(Color.rgb(174,160,203));

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
                //else
                    //graphicsContext.fillRect(x, y, cellWidth, cellHeight);
            }

        }
    }
}
