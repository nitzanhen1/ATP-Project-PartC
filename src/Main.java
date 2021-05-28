import View.MainViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View/MainView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Friends Maze");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("View/MainStyle.css").toExternalForm());
        primaryStage.setScene(scene);

        MainViewController mainViewController =fxmlLoader.getController();
        mainViewController.setStage(primaryStage);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                mainViewController.exit();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
