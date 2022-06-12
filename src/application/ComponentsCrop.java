package application;

import components.sep;
import components.timeStamps;
import components.volume;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ComponentsCrop {
	static Boolean bypassed = false;
	static Boolean tabPress = false;
	private TextField xText, yText, wText, hText;
	private volume vol;
	private timeStamps time;
	VideoDetails v;

	public BorderPane cropPane(VideoDetails vid) {
		BorderPane border = new BorderPane();
		BorderPane borderTop = new BorderPane();
		Label description = new Label("Crop - starting from top left corner (0,0), moves X pixels right and Y pixels down,"
				+ " then crops inputted resolution. \n(Original File Resolution - XY Values = New Max Resolution)");

		Label timestamp = new Label("Timestamps:");
		VBox vbox = new VBox(5);
		
		HBox xyHbox = new HBox(5);
 		VBox xVbox = new VBox(5);
		VBox yVbox = new VBox(5);
		VBox xyVbox = new VBox(5);
		xText = new TextField("0");
		yText = new TextField("0");
		Label xLabel = new Label("X : ");
		Label yLabel = new Label("Y : ");
		Label startPosition = new Label("Starting Position:");

		assignText(xText);
		assignText(yText);
		xVbox.getChildren().addAll(xLabel, yLabel);
		yVbox.getChildren().addAll(xText, yText);
		xyHbox.getChildren().addAll(xVbox, yVbox);
		xyVbox.getChildren().addAll(startPosition, xyHbox);
		
		HBox whHbox = new HBox(5);
 		VBox wVbox = new VBox(5);
		VBox hVbox = new VBox(5);
		VBox whVbox = new VBox(5);
		wText = new TextField("0");
		hText = new TextField("0");
		Label wLabel = new Label("Width : ");
		Label hLabel = new Label("Height : ");
		Label resolution = new Label("Resolution:");
		
		vol = new volume();
		time = new timeStamps();
		v = vid;
		
		assignText(hText);
		assignText(wText);
		wVbox.getChildren().addAll(wLabel, hLabel);
		hVbox.getChildren().addAll(wText, hText);
		whHbox.getChildren().addAll(wVbox, hVbox);
		whVbox.getChildren().addAll(resolution, whHbox);
		whVbox.setMaxWidth(150);
		
		VBox timeBox = new VBox();
		
		timeBox.getChildren().addAll(timestamp, time.timeStampsLayout());
		timeBox.setAlignment(Pos.TOP_LEFT);
		whVbox.setAlignment(Pos.CENTER_LEFT);
		xyVbox.setAlignment(Pos.CENTER_LEFT);
		
		borderTop.setLeft(xyVbox);
		borderTop.setCenter(whVbox);
		borderTop.setRight(timeBox);
		
		border.setId("crop");
		description.setWrapText(true);
		vbox.getChildren().addAll(description, new sep().getSep(), borderTop, new sep().getSep(),
				vol.vol(), new sep().getSep());
		vbox.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
		
		border.setTop(vbox);
		
		return border;
	}
	
	public static void assignText(TextField t) {
		t.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				Platform.runLater(t::selectAll);
            }
		});
		t.addEventFilter(KeyEvent.KEY_TYPED, new keyHandler(t));
		t.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.TAB)
					tabPress = true;
			}
		});
		t.setOnMouseClicked(e -> {
			t.selectAll();
			bypassed = true;
		});
		t.focusedProperty().addListener((observable, oldVal, newVal) -> {
			if(oldVal) {
				if(t.getText().length() == 0) {
					t.setText("0");
				}
			}
		});
	}
	
	static class keyHandler implements EventHandler<KeyEvent>{
		TextField t = new TextField();
		
		keyHandler(TextField t){
			this.t = t;
		}
		@Override
		public void handle(KeyEvent event) {
			char c = event.getCharacter().charAt(0);
			if(!(Character.isDigit(c))) {
				event.consume();
			}
			if(t.getText().length() > 3 && !bypassed) {
				event.consume();
			}
			if(tabPress) {
				bypassed = true;
				tabPress = false;
			}
			else {
				bypassed = false;
			}
		}
	}
	
	public int getX() {
		return Integer.parseInt(xText.getText());
	}
	
	public int getY() {
		return Integer.parseInt(yText.getText());
	}
	
	public int getW() {
		return Integer.parseInt(wText.getText());
	}
	
	public int getH() {
		return Integer.parseInt(hText.getText());
	}
	
	public volume getVol() {
		return vol;
	}
	
	public timeStamps getTime() {
		return time;
	}
	
	public boolean validate() {
		int x = this.getX();
		int y = this.getY();
		int w = this.getW();
		int h = this.getH();
		int vidW = v.getWidth();
		int vidH = v.getHeight();
		if(x > vidW || w > vidW || y > vidH || h > vidH) {
			return false;
		}
		if(((x * y) + (w * h)) > (vidW * vidH)) {
			return false;
		}
		if(x + w > vidW || (y + h) > vidH) {
			return false;
		}
		
		return true;
	}
}
