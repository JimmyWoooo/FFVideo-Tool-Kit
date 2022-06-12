package application;

import components.scaleOptions;
import components.sep;
import components.timeStamps;
import components.volume;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ComponentsTrim {
	volume vol;
	scaleOptions scale;
	timeStamps time;
	
	public BorderPane trimPane(VideoDetails vid) {
		BorderPane border = new BorderPane();
		Label timestamp = new Label("Timestamps:");
		VBox vbox = new VBox();
		VBox vboxVol = new VBox();
		vol = new volume();
		scale = new scaleOptions();
		time = new timeStamps();
		
		vbox.getChildren().addAll(timestamp, time.timeStampsLayout(), 
				new sep().getSep(), scale.createScale(), new sep().getSep());
		vboxVol.getChildren().addAll(vol.vol(), new sep().getSep());
		
		timestamp.setPadding(new Insets(0, 0, 5, 0));
		border.setTop(vbox);
		border.setLeft(vboxVol);
		
		return border;
	}
}
