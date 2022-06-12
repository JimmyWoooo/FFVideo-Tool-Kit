package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import components.timeStamps;
import components.volume;
import components.m3u8Input;

public class ComponentsM3U8{
	private RadioButton convert;
	private RadioButton copy;
	private timeStamps time;
	private m3u8Input m;
	private volume v;
	private VideoDetails video;
	
	public BorderPane m3u8Pane(VideoDetails vid) {
		BorderPane border = new BorderPane();

		video = vid;
		border.setPrefSize(640, 650);
		border.setId("m3u8");
		border.setTop(filePaths(video));
		
		return border;
	}
	
	public  BorderPane filePaths(VideoDetails vid) {
		BorderPane border = new BorderPane();
		Label timestamp = new Label("Timestamps:");
		VBox radio = new VBox();
		copy = new RadioButton("Copy (Larger output but faster)");
		convert = new RadioButton("Convert (Smaller output but slower)");
		ToggleGroup group = new ToggleGroup();
		m = new m3u8Input();
		time = new timeStamps();
		v = new volume();
		
		copy.setSelected(true);
		copy.setToggleGroup(group);
		convert.setToggleGroup(group);
		radio.setPadding(new Insets(10, 0, 0, 0));
		timestamp.setPadding(new Insets(10, 0, 0, 0));
		
		radio.getChildren().addAll(copy, convert, timestamp);
		radio.setAlignment(Pos.BOTTOM_LEFT);
		
		
		BorderPane b = new BorderPane();
		b.setBottom(radio);
		b.setTop(m.createM3U8(vid));
		
		border.setTop(b);
		border.setLeft(time.timeStampsLayout());
		border.setBottom(v.vol());
		
		return border;
	}
	
	public String getRadioSelected() {
		if(copy.isSelected()) {
			return "copy";
		}else
			return "convert";
	}
	
	public timeStamps getTimeStampsClass() {
		return time;
	}
	
	public m3u8Input getM3U8() {
		return m;
	}
	
	public volume getVol() {
		return v;
	}
}
