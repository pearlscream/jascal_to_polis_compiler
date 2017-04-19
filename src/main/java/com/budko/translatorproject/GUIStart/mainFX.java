package com.budko.translatorproject.GUIStart;/**
 * @author DBudko.
 */

import com.budko.translatorproject.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("form.fxml"));
//        loader.setLocation(getClass().getResource("../view/form.fxml"));
        Parent fxmlMain = loader.load();
        MainController controller = loader.getController();
        primaryStage.setTitle("Jascal lexical analyser");
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1200);
        primaryStage.setScene(new Scene(fxmlMain, 300, 275));
        primaryStage.show();
    }
}
