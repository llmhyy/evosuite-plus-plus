package visualization.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import visualization.utils.Constants;

public class MainController {

	@FXML
	private ToolBarController toolBarController;

	@FXML
	private TestCaseController testCaseController;

	@FXML
	private GraphImageController graphImageController;

	public Stage stage;

	private String fName;
	private String branchName;

	private File testFolder;
	private List<File> testFiles;
	private List<File> graphFiles;
//	private File leftTestFile;
//	private File rightTestFile;
	private File graphImageFile;

	private int leftTestFileIndex;

	@FXML
	public void initialize() {
		System.out.println("Application started");
		toolBarController.init(this);
//		fName = "D:\\linyun\\test\\I110 Branch 43 IFNONNULL L103";
//		String leftTestCaseName = "40-CreditAuthInfo#equals#74.txt";
//		String rightTestCaseName = "41-CreditAuthInfo#equals#117.txt";
//		loadFiles(fName + "\\" + leftTestCaseName, fName + "\\" + rightTestCaseName, "");
	}
	
	public void openFileSelector() {
		toolBarController.openFileSelector();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	/**
	 * Initialize and load all three files, given the chosen left test case file
	 * 
	 * @param leftTestFile
	 */
	public void initializeFiles(File leftTestFile) {

		// Initialize branch name and folder
		testFolder = leftTestFile.getParentFile();
		branchName = testFolder.getName();

		// Initialize test files
		File[] testFilesArr = testFolder.listFiles((dir, name) -> name.matches(Constants.TEST_FILE_REGEX));
		testFiles = Arrays.asList(testFilesArr);
		
		// sort files by starting ID
		testFiles.sort((left, right) -> {
			int leftID = Integer.parseInt(left.getName().split("-")[0]);
			int rightID = Integer.parseInt(right.getName().split("-")[0]);
			return leftID - rightID;
		});
		
		// Initialize graph image files
		File[] graphFilesArr = testFolder
				.listFiles((dir, name) -> name.matches(branchName + Constants.GRAPH_IMAGE_SUFFIX));
		graphFiles = Arrays.asList(graphFilesArr);
		graphImageFile = graphFiles.size() == 1 ? graphFiles.get(0) : new File("");

		// Initialize index of left test case file
		leftTestFileIndex = testFiles.indexOf(leftTestFile);

		// Load and display test cases and graph image
		loadFiles();
	}
	
	public void loadPrevious() {
		leftTestFileIndex -= 1;
		loadFiles();
	}
	
	public void loadNext() {
		leftTestFileIndex += 1;
		loadFiles();
	}

	private void loadFiles() {
		checkButtonsToDisable();

		File leftTestFile = testFiles.get(leftTestFileIndex);
		File rightTestFile = testFiles.size() > 1 ? testFiles.get(leftTestFileIndex + 1) : new File("");
		File graphFile = graphImageFile;

		testCaseController.setLeftTestCase(leftTestFile);
		testCaseController.setRightTestCase(rightTestFile);
		graphImageController.setGraphImage(graphFile);
	}
	
	private void checkButtonsToDisable() {
		if (testFiles.size() <= 2) {
			// 2 or less test files, disable both button
			toolBarController.setPreviousDisabled(true);
			toolBarController.setNextDisabled(true);
		} else if (leftTestFileIndex == 0) {
			// Reached first test case
			toolBarController.setPreviousDisabled(true);
		} else if (leftTestFileIndex == testFiles.size() - 2) {
			// Reached last test case
			toolBarController.setNextDisabled(true);
		} else {
			// Enable buttons
			toolBarController.setPreviousDisabled(false);
			toolBarController.setNextDisabled(false);
		}
	}
}
