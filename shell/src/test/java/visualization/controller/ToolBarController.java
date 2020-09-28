package visualization.controller;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class ToolBarController {
	@FXML
	private Button prevButton;
	
	@FXML
	private Button nextButton;
	
	private MainController main;

	@FXML
	public void initialize() {
		System.out.println("Toolbar initializing");
	}
	
	public void init(MainController mainController) {
		main = mainController;
	}

	public void openFileSelector() {
		FileChooser fileChooser = new FileChooser();
		File leftTestFile = fileChooser.showOpenDialog(main.getStage());
		
		if (leftTestFile == null) {
			setPreviousDisabled(true);
			setNextDisabled(true);
			return;
		}
		
		main.initializeFiles(leftTestFile);
	}
	
	public void handlePrevious() {
		main.loadPrevious();
	}
	
	public void handleNext() {
		main.loadNext();
	}
	
	public void setPreviousDisabled(boolean flag) {
		prevButton.setDisable(flag);
	}
	
	public void setNextDisabled(boolean flag) {
		nextButton.setDisable(flag);
	}
}
