package com.jonesun.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jonesun.bootstrap.StartupView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.update4j.LaunchContext;
import org.update4j.SingleInstanceManager;
import org.update4j.inject.InjectTarget;
import org.update4j.inject.PostInject;
import org.update4j.service.Launcher;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaFxLauncher extends Application implements Launcher {

	private static final Path LOCK_DIR = Paths.get(System.getProperty("user.home"), ".update4j-demo");

	@Override
	public long version() {
		return 0;
	}

	@InjectTarget
	private CheckBox singleInstanceCheckbox;

	@InjectTarget
	private TextField singleInstanceMessage;

	@InjectTarget
	private CheckBox newWindowCheckbox;

	@InjectTarget
	private Stage primaryStage;

	/*
	 * I use @PostInject instead of @InjectTarget to demonstrate how to use it
	 */
	private StartupView startup;

	@PostInject
	private void getStartupView(StartupView view) {
		startup = view;
	}

	@InjectTarget
	private Image inverted;

	private LoadingView loading;
	private Stage stage;

	@Override
	public void run(LaunchContext ctx) {

		Parent libs;
		try {
			libs = FXMLLoader.load(getClass().getResource("/sample.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			libs = new LibraryView();
		}

		Scene scene = new Scene(libs);
		scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
		scene.getStylesheets()
						.add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-fonts.css").toExternalForm());
		scene.getStylesheets()
						.add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-design.css")
										.toExternalForm());

		// We call these methods since it take about 1.5 seconds
		// to render and locks UI thread.
		libs.applyCss();
		libs.layout();

		Platform.runLater(() -> {
			stage = new Stage();
            stage.setTitle("Update4j Demo Business");
            stage.setMinWidth(650);
            stage.setMinHeight(500);
			stage.setScene(scene);
			stage.getIcons().setAll(primaryStage.getIcons());
			stage.show();
			primaryStage.hide();
		});

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		Parent libs;
		try {
			libs = FXMLLoader.load(getClass().getResource("/sample.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			libs = new LibraryView();
		}

		Scene scene = new Scene(libs);
//		scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
		scene.getStylesheets()
				.add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-fonts.css").toExternalForm());
		scene.getStylesheets()
				.add(JFXButton.class.getResource("/com/jfoenix/assets/css/jfoenix-design.css")
						.toExternalForm());

		// We call these methods since it take about 1.5 seconds
		// to render and locks UI thread.
		libs.applyCss();
		libs.layout();

		stage.setTitle("Update4j Demo Business");
		stage.setMinWidth(650);
		stage.setMinHeight(500);
		stage.setScene(scene);
//		stage.getIcons().setAll(primaryStage.getIcons());
		stage.show();

	}
}
