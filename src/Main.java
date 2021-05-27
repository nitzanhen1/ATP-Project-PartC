import View.MyViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View/MainView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Friends Maze");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("View/MainStyle.css").toExternalForm());
        primaryStage.setScene(scene);

        MyViewController myViewController =fxmlLoader.getController();
        myViewController.setStage(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
