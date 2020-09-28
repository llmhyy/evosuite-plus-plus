package visualization.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TestCaseController {
	@FXML
	private TextArea leftTextArea;

	@FXML
	private TextArea rightTextArea;
	
	@FXML
	private Label leftLabel;
	
	@FXML
	private Label rightLabel;

	@FXML
	private void initialize() {
		System.out.println("TestCase initializing");
	}
	
	private String getTestCaseContent(File file) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();
			
			return stringBuilder.toString();
		} catch (FileNotFoundException e) {
			System.out.println("Test case file not found: " + file.getPath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading test case");
			e.printStackTrace();
		}

		return "";
	}
	
	public void setLeftTestCase(File file) {
		String content = getTestCaseContent(file);
		leftTextArea.setText(content);
		leftLabel.setText(file.getName().replace("-", ". ").replace(".txt", ""));
	}
	
	public void setRightTestCase(File file) {
		String content = getTestCaseContent(file);
		rightTextArea.setText(content);
		rightLabel.setText(file.getName().replace("-", ". ").replace(".txt", ""));
	}
}
