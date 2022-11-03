package org.benja.tpu_tsb_2.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        javafx.scene.Parent sceneParent = fxmlLoader.load();
        VBox sceneRoot = fxmlLoader.getRoot();
        Scene scene = new Scene(sceneParent, sceneRoot.getPrefWidth(), sceneRoot.getPrefHeight());
        stage.setTitle("Consultar series");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}