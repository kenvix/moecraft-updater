//--------------------------------------------------
// Class UpdaterIndex
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.moecraft.generator.Environment;

import java.net.URL;

public class UpdaterIndex extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL    resource = getClass().getResource("index.fxml");
        Parent root     = FXMLLoader.load(resource);
        primaryStage.setTitle(Environment.getAppName());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public static void display() {
        launch();
    }
}
