package application;

import components.m3u8Input;
import components.sep;
import components.timeStamps;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ComponentsDecimate {
	private CheckBox m3u8; 
	private timeStamps time;
	private m3u8Input m;

	public BorderPane decimatePane(VideoDetails vid) {
		BorderPane border = new BorderPane();
		Label description = new Label("Decimate - takes the input file and cuts out duplicated frames."
				+ " There will be no audio for this.");
		Label timestamp = new Label("Timestamps:");
		m3u8 = new CheckBox("M3U8:");
		VBox vbox = new VBox(5);
		VBox m3u8Vbox = new VBox(5);
		m = new m3u8Input();
		time = new timeStamps();
		
		border.setId("decimate");
		description.setWrapText(true);
		timestamp.setPadding(new Insets(0, 0, 5, 0));
		
		m3u8Vbox.setDisable(true);
		m3u8Vbox.getChildren().addAll(m.createM3U8(vid));
		vbox.getChildren().addAll(description, new sep().getSep(), m3u8, m3u8Vbox, new sep().getSep(), 
				timestamp, time.timeStampsLayout(), new sep().getSep());
		
		m3u8.setOnAction(e -> m3u8Vbox.setDisable(!m3u8Vbox.isDisabled()));
		
		border.setTop(vbox);
		
		return border;
	}
	
	public timeStamps getTimeStampsClass() {
		return time;
	}
	
	public m3u8Input getM3U8() {
		return m;
	}
	
	public boolean m3u8Check() {
		return m3u8.isSelected();
	}
}
