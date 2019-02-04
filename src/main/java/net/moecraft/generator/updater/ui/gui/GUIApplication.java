//--------------------------------------------------
// Class GUIApplication
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import net.moecraft.generator.Environment;

import java.net.URL;

public class GUIApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL    resource = getClass().getResource("nodeselect.fxml");
        Parent root     = FXMLLoader.load(resource);
		
        primaryStage.setTitle(Environment.getAppName());
        primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest((event) -> {Platform.exit();});
        primaryStage.show();
    }
    public static void display() {
        launch();
    }
}
