package View;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface IView {
    void generateMaze(ActionEvent actionEvent);
    void keyPressed(KeyEvent keyEvent);
    void solveMaze(ActionEvent actionEvent);
    public void mouseClicked(MouseEvent mouseEvent);
}
