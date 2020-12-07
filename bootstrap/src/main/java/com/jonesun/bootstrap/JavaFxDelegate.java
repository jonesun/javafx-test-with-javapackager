package com.jonesun.bootstrap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.update4j.Configuration;
import org.update4j.service.Delegate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFxDelegate extends Application implements Delegate {

    public static List<Image> images;
    public static Image inverted;

    @Override
    public void init() throws Exception {
//		super.init();
        System.setProperty("update4j.suppress.warning", "true");

        List<String> sizes = List.of("tiny", "small", "medium", "large", "xlarge");
        images = sizes.stream()
                .map(s -> ("/icons/update4j-icon-" + s + ".png"))
                .map(s -> getClass().getResource(s).toExternalForm())
                .map(Image::new)
                .collect(Collectors.toList());
        inverted = new Image("/icons/update4j-icon-invert.png");
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(rootNode, 300, 275));
//        primaryStage.show();

        URL configUrl = new URL("http://192.168.31.13/resource/demo/app/config.xml");
        Configuration config = null;
        try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8)) {
            config = Configuration.read(in);
        } catch (IOException e) {
            System.err.println("Could not load remote config, falling back to local.");
            try (Reader in = Files.newBufferedReader(Paths.get("app/config.xml"))) {
                config = Configuration.read(in);
            }
        }

        StartupView startup = new StartupView(config, primaryStage);

        Scene scene = new Scene(startup);
        scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());

        primaryStage.getIcons().addAll(images);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Update4j Demo Launcher");
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public void main(List<String> list) throws Throwable {
        launch();
    }
}
