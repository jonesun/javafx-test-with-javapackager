package com.jonesun.app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.update4j.LaunchContext;
import org.update4j.inject.InjectTarget;
import org.update4j.service.Launcher;

import java.io.IOException;

@SpringBootApplication
public class JavaFxLauncher extends Application implements Launcher {

    private ConfigurableApplicationContext context;

    @Override
    public long version() {
        return 0;
    }

    @InjectTarget
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        show(stage, getParent("test"));
    }

    @Override
    public void run(LaunchContext ctx) {
        try {
            String activeProfiles = ctx.getConfiguration().getDynamicProperties().getOrDefault("profiles.active", "dev");
            Parent parent = getParent(activeProfiles);
            Platform.runLater(() -> {
                show(new Stage(), parent);
                primaryStage.hide();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Parent getParent(String activeProfiles) throws IOException {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(JavaFxLauncher.class);
        context = builder.profiles(activeProfiles).run();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        loader.setControllerFactory(context::getBean);
        return loader.load();
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
