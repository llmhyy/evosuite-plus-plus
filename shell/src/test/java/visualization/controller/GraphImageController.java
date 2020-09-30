package visualization.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;

public class GraphImageController {
	@FXML
	private ScrollPane scrollPane;

	@FXML
	private ImageView imageView;

	private File graphFile;
	
	final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
	
	@FXML
	public void initialize() {
		System.out.println("GraphImage initializing");

		zoomProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				imageView.setFitWidth(zoomProperty.get() * 4);
				imageView.setFitHeight(zoomProperty.get() * 3);
			}
		});

		scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					zoomProperty.set(zoomProperty.get() * 1.1);
				} else if (event.getDeltaY() < 0) {
					zoomProperty.set(zoomProperty.get() / 1.1);
				}
			}
		});

	}

	public void setGraphImage(File file) {
		if (file.equals(graphFile)) {
			return;
		}
		
		try {
			Image image = new Image(new FileInputStream(file));
			imageView.setImage(image);
			graphFile = file;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
