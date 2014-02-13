package eu.mihosoft.vrl.ui.codevisualization;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    private MainWindowController controller;

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        controller = fxmlLoader.getController();
        
        Scene scene = new Scene((Parent) fxmlLoader.getRoot(), 1200, 800);

        primaryStage.setTitle("VRL Demo!");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        controller.loadTextFile(new File("Sample3.groovy"));
        
        scene.getStylesheets().add("/eu/mihosoft/vrl/ui/codevisualization/default.css");
    }

   
}