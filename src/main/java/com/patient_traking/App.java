package com.patient_traking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AccessOpt.fxml"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false); //lo user non può massimizzare la finestra
        primaryStage.setTitle("Opzioni di accesso");

        Image image = new Image(getClass().getResourceAsStream("/AppIcon.png"));
        primaryStage.getIcons().add(image);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}