package com.jonesun.bootstrap;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.UpdateOptions;
import org.update4j.inject.InjectSource;
import org.update4j.inject.Injectable;
import org.update4j.service.UpdateHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StartupView extends FXMLView implements UpdateHandler, Injectable {

	private Configuration config;

	@FXML
	private Label status;

	@FXML
	private ImageView image;

	@FXML
	private GridPane launchContainer;

	@FXML
	@InjectSource
	private CheckBox singleInstanceCheckbox;

	@FXML
	@InjectSource
	private TextField singleInstanceMessage;

	@FXML
	@InjectSource
	private CheckBox newWindowCheckbox;

	@FXML
	private GridPane updateContainer;

	@FXML
	private Pane primary;

	@FXML
	private Pane secondary;

	@FXML
	private StackPane progressContainer;
	
	@FXML
	private CheckBox slow;

	@FXML
	private Button update;

	@FXML
	private Button launch;

	@FXML
	private SVGPath updatePath;

	@FXML
	private SVGPath cancelPath;

	private DoubleProperty primaryPercent;
	private DoubleProperty secondaryPercent;

	private BooleanProperty running;
	private volatile boolean abort;

	@InjectSource
	private Stage primaryStage;

	@InjectSource
	private Image inverted = JavaFxDelegate.inverted;

	public StartupView(Configuration config, Stage primaryStage) {
		this.config = config;
		this.primaryStage = primaryStage;

		image.setImage(JavaFxDelegate.inverted);

		primaryPercent = new SimpleDoubleProperty(this, "primaryPercent");
		secondaryPercent = new SimpleDoubleProperty(this, "secondaryPercent");

		running = new SimpleBooleanProperty(this, "running");

		primary.maxWidthProperty().bind(progressContainer.widthProperty().multiply(primaryPercent));
		secondary.maxWidthProperty().bind(progressContainer.widthProperty().multiply(secondaryPercent));

		status.setOpacity(0);
		FadeTransition fade = new FadeTransition(Duration.seconds(1.5), status);
		fade.setToValue(0);

		running.addListener((obs, ov, nv) -> {
			if (nv) {
				fade.stop();
				status.setOpacity(1);
			} else {
				fade.playFromStart();
				primaryPercent.set(0);
				secondaryPercent.set(0);
			}
		});

		primary.visibleProperty().bind(running);
		secondary.visibleProperty().bind(primary.visibleProperty());

		cancelPath.visibleProperty().bind(running);
		updatePath.visibleProperty().bind(cancelPath.visibleProperty().not());

		singleInstanceMessage.disableProperty().bind(singleInstanceCheckbox.selectedProperty().not());

		TextSeparator launchSeparator = new TextSeparator("Launch");
		launchContainer.add(launchSeparator, 0, 0, GridPane.REMAINING, 1);

		TextSeparator updateSeparator = new TextSeparator("Update");
		updateContainer.add(updateSeparator, 0, 0, GridPane.REMAINING, 1);
	}

	@FXML
	void launchPressed(ActionEvent event) {
		Task<Boolean> checkUpdates = checkUpdates();

		checkUpdates.setOnSucceeded(evt -> {
			Thread run = new Thread(() -> {
				config.launch(this);
			});

			//FIXME: add opt-out checkbox
			if (checkUpdates.getValue()) {
				ButtonType launch = new ButtonType("Launch", ButtonData.OK_DONE);
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText("Update required");
				alert.setContentText("Application is not up-to-date, launch anyway?");
				alert.getButtonTypes().setAll(ButtonType.CANCEL, launch);

				alert.showAndWait().filter(bt -> bt == launch).ifPresent(bt -> {
					run.start();
				});
			} else {
				run.start();
			}
		});

		run(checkUpdates);
	}

	@FXML
	void updatePressed(ActionEvent event) {
		if (running.get()) {
			abort = true;
			return;
		}

		running.set(true);

		status.setText("Checking for updates...");

		Task<Boolean> checkUpdates = checkUpdates();
		checkUpdates.setOnSucceeded(evt -> {
			if (!checkUpdates.getValue()) {
				status.setText("No updates found");
				running.set(false);
			} else {
				Task<Void> doUpdate = new Task<>() {

					@Override
					protected Void call() throws Exception {
//					    Path zip = Paths.get("app-update.zip");
						Path zip = new File(System.getProperty("user.dir"), "app-update.zip").toPath();
						if(config.update(UpdateOptions.archive(zip).updateHandler(StartupView.this)).getException() == null) {
						    Archive.read(zip).install();
						}

						return null;
					}

				};

				run(doUpdate);
			}
		});

		run(checkUpdates);
	}

	private Task<Boolean> checkUpdates() {
		return new Task<>() {

			@Override
			protected Boolean call() throws Exception {
				return config.requiresUpdate();
			}

		};
	}

	private void run(Runnable runnable) {
		Thread runner = new Thread(runnable);
		runner.setDaemon(true);
		runner.start();
	}

	/*
	 * UpdateHandler methods
	 */
	@Override
	public void updateDownloadFileProgress(FileMetadata file, float frac) throws AbortException {
		Platform.runLater(() -> {
			status.setText("Downloading " + file.getPath().getFileName() + " (" + ((int) (100 * frac)) + "%)");
			secondaryPercent.set(frac);
		});

		if (abort) {
			throw new AbortException();
		}
	}

	@Override
	public void updateDownloadProgress(float frac) throws InterruptedException {
		Platform.runLater(() -> primaryPercent.set(frac));
		
		if(slow.isSelected())
			Thread.sleep(100);
	}

	@Override
	public void failed(Throwable t) {
		Platform.runLater(() -> {
			if (t instanceof AbortException)
				status.setText("Update aborted");
			else {
				status.setText("Failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Failed");
				alert.setHeaderText("发生异常");
				alert.setContentText("Failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
				alert.showAndWait();
			}
		});
	}

	@Override
	public void succeeded() {
		Platform.runLater(() -> status.setText("Download complete"));
	}

	@Override
	public void stop() {
		Platform.runLater(() -> running.set(false));
		abort = false;
	}

}
