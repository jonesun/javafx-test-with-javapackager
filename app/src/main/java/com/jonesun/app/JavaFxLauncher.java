package com.jonesun.app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.update4j.LaunchContext;
import org.update4j.inject.InjectTarget;
import org.update4j.service.Launcher;

import java.io.IOException;

public class JavaFxLauncher extends Application implements Launcher {

    @Override
    public long version() {
        return 0;
    }

    @InjectTarget
    private Stage primaryStage;

    @Override
    public void run(LaunchContext ctx) {
        try {
            Parent parent = getParent();
            Platform.runLater(() -> {
                show(new Stage(), parent);
                primaryStage.hide();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = getParent();
        show(stage, parent);
    }

    private Parent getParent() throws IOException {
        return FXMLLoader.load(getClass().getResource("/sample.fxml"));
    }

    private void show(Stage stage, Parent parent) {
        Scene scene = new Scene(parent);
        scene.getStylesheets()
                .add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-fonts.css").toExternalForm());
        scene.getStylesheets()
                .add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-design.css")
                        .toExternalForm());

        parent.applyCss();
        parent.layout();
        stage.setTitle("Update4j Demo Business");
        stage.setMinWidth(650);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }
}
