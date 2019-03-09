import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage getStage() {
        return stage;
    }

    private static void setStage(Stage stage) {
        Main.stage = stage;
    }

    private void setScene(Scene scene) {
        this.scene = scene;
    }

    private static Scene scene;
    private static Stage stage;


    @Override
    public void start(Stage primaryStage) throws Exception{
        setStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("intro.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        setScene(new Scene(root));
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
