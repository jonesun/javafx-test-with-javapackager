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
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        //一定要修改FXMLLoader的默认类加载器，否则使用bootstrap启动器启动时fxml中会由于无法加载到类而报错(详细参考FXMLLoader源码)
        FXMLLoader.setDefaultClassLoader(this.getClass().getClassLoader());
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/sample.fxml")), null, null, context::getBean, StandardCharsets.UTF_8);
    }

    private void show(Stage stage, Parent parent) {
        Scene scene = new Scene(parent);
        scene.getStylesheets()
                .addAll(
                        Objects.requireNonNull(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-fonts.css")).toExternalForm(),
                        Objects.requireNonNull(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-design.css")).toExternalForm(),
                        Objects.requireNonNull(JFXButton.class.getResource("/css/common.css")).toExternalForm()
                );

        parent.applyCss();
        parent.layout();
        stage.setTitle("Update4j Demo Business");
        stage.setMinWidth(650);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }
}
