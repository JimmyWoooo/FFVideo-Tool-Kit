package components;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class scaleOptions {
	String resolutions[] = {"Same", "1920 x 1080", "1280 x 720", "854 x 480"};
	ComboBox<String> cb;
	
	public BorderPane createScale() {
		BorderPane border = new BorderPane();
		Label title = new Label("Scale:");
		cb = new ComboBox<>();
		HBox hbox = new HBox(5);
		
		cb.getItems().addAll(resolutions);
		cb.getSelectionModel().selectFirst();
		
		hbox.getChildren().addAll(title, cb);
		hbox.setPrefHeight(50);
		hbox.setAlignment(Pos.CENTER_LEFT);
		border.setTop(hbox);
		
		return border;
	}
}
