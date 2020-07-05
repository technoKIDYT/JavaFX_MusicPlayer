package application;

import java.net.URL;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	private double xOffset, yOffset;
	public static Stage waitStage, loginStage;
	public static final String uiPath = "file:\\" + System.getProperty("user.dir") + "\\resources\\";
	private FXMLLoader fxmlLoader;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent wait = null;
		waitStage = new Stage();
		fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(new URL(uiPath + "Player.fxml"));
		wait = fxmlLoader.load();
		waitStage.initStyle(StageStyle.UNDECORATED);
		wait.autosize();
		Scene scene = new Scene(wait);
		waitStage.setTitle("Login Sign Up");
		scene.setFill(Color.TRANSPARENT);
		waitStage.setResizable(false);
		waitStage.setScene(scene);
		waitStage.show();

		wait.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		wait.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				waitStage.setX(event.getScreenX() - xOffset);
				waitStage.setY(event.getScreenY() - yOffset);
			}
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}
