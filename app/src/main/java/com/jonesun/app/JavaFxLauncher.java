package com.jonesun.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jonesun.bootstrap.StartupView;
import javafx.application.Platform;
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

public class JavaFxLauncher implements Launcher {

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
	private static Stage stage;

	@Override
	public void run(LaunchContext ctx) {
		if (singleInstanceCheckbox.isSelected()) {
			try {
				Files.createDirectories(LOCK_DIR);
			} catch (IOException e) {
				e.printStackTrace();
			}

			SingleInstanceManager.execute(List.of(singleInstanceMessage.getText()), args -> {
				ButtonType dismiss = new ButtonType("Dismiss", ButtonData.OK_DONE);
				Platform.runLater(() -> showDialog("Launcher Message", args.get(0), dismiss));
			}, LOCK_DIR);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					Thread.sleep(5000);
					if (isEmptyDirectory(LOCK_DIR)) {
						Files.deleteIfExists(LOCK_DIR);
					}
				} catch (Exception e) {
				}
			}));
		}

		Platform.runLater(() -> {

			stage = newWindowCheckbox.isSelected() ? new Stage() : primaryStage;
			
			// we use FXML loading internally here
			loading = new LoadingView("Rendering Nodes");

			startup.getChildren().add(loading);
			loading.show();
		});

		LibraryView libs = new LibraryView();
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
			stage.setTitle("Update4j Demo Business");
			stage.setMinWidth(650);
			stage.setMinHeight(500);

			stage.setScene(scene);

			if (newWindowCheckbox.isSelected()) {
				stage.getIcons().setAll(primaryStage.getIcons());
				stage.show();
				primaryStage.hide();
			}

		});

	}

	private static boolean isEmptyDirectory(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> dir = Files.newDirectoryStream(path)) {
				return !dir.iterator().hasNext();
			}
		}

		return false;
	}

	public static ButtonType showDialog(String header, String message, ButtonType... buttonTypes) {
		JFXDialogLayout layout = new JFXDialogLayout();

		Label messageLabel = new Label(message);
		layout.setBody(messageLabel);

		Label caption = new Label(header);
		layout.setHeading(caption);

		List<JFXButton> buttons = new ArrayList<>();

		JFXDialog alert = new JFXDialog();

		ButtonType[] pressed = new ButtonType[1];
		for (ButtonType type : buttonTypes) {
			JFXButton b = new JFXButton(type.getText().toUpperCase());
			b.setDefaultButton(type.getButtonData().isDefaultButton());
			b.setCancelButton(type.getButtonData().isCancelButton());

			b.setOnAction(evt -> {
				alert.close();
				pressed[0] = type;
			});

			buttons.add(b);
		}
		layout.setActions(buttons);

		alert.setContent(layout);
		alert.show((StackPane) stage.getScene().getRoot());

		stage.getScene().getRoot().requestFocus();
		stage.toFront();

		return pressed[0];
	}
}
