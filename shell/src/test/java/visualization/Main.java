package visualization;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import visualization.controller.MainController;
import visualization.utils.Constants;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		try {
			// Load FXML and pass stage to MainController
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.MAIN_FXML));
			Parent root = (Parent) fxmlLoader.load();
			MainController mainController = (MainController) fxmlLoader.getController();
			mainController.setStage(stage);

			// Display stage
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Object Graph Construction Visualizer");
			stage.show();

			// Start by opening file selector
			mainController.openFileSelector();

		} catch (IOException e) {
			System.out.println("Cannot find Main.fxml: " + Constants.MAIN_FXML);
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		launch(args);
	}
}
