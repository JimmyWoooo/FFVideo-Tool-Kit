package components;

import handlers.changeListener;
import handlers.scrollHandler;
import handlers.scrollSlider;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class volume {
	static Label ear;
	Boolean bypassed = false;
	Boolean tabPress = false;
	int max;
	Slider slider;
	RadioButton normal, custom;
	
	public BorderPane vol() {
		BorderPane border = new BorderPane();
		Label volumeLabel = new Label("Volume (%):");
		normal = new RadioButton("Normal (100%)");
		custom = new RadioButton("Custom:");
		slider = new Slider(0, 200, 100);
		VBox vbox = new VBox(5);
		HBox hbox = new HBox(5);
		HBox hbox2 = new HBox(350);
		Spinner<Integer> spinner = new Spinner<>(0, 200, 100);
		ToggleGroup group = new ToggleGroup();
		ear = new Label("Ears Go BOOM!!!");
		
		normal.setToggleGroup(group);
		custom.setToggleGroup(group);
		normal.setSelected(true);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMinWidth(430);
		slider.setPadding(new Insets(10, 0, 0, 0));
		spinner.setId("spinner");
		ear.setVisible(false);
		
		this.assignEvents(spinner);
		spinner.setOnScroll(new scrollHandler(spinner, slider));
		spinner.setEditable(true);
		slider.valueProperty().addListener(new changeListener(spinner, ear));
		slider.setOnScroll(new scrollSlider(slider));
		
		hbox.setDisable(true);
		normal.setOnAction(e->hbox.setDisable(true));
		custom.setOnAction(e->hbox.setDisable(false));
		
		hbox.getChildren().addAll(slider, spinner);
		hbox2.getChildren().addAll(custom, ear);
		hbox2.setAlignment(Pos.BOTTOM_LEFT);
		vbox.getChildren().addAll(volumeLabel, normal, hbox2, hbox);
		border.setTop(vbox);
		
		return border;
	}
	
	public void assignEvents(Spinner<Integer> spin) {
		spin.setOnScroll(new scrollHandler(spin, bypassed));
		spin.getEditor().addEventFilter(KeyEvent.KEY_TYPED, new keyHandler(spin));
		spin.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.TAB)
					tabPress = true;
			}
		});
		spin.getEditor().setOnMouseClicked(e -> {
			spin.getEditor().selectAll();
			bypassed = true;
		});
		spin.setEditable(true);
		spin.getEditor().end();
		spin.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				Platform.runLater(spin.getEditor()::selectAll);
				bypassed = true;
            }
			if(oldValue) {
				if(spin.getEditor().getText().length() == 0) {
					spin.getValueFactory().setValue(1);
					spin.decrement();
				}
				if(spin.getEditor().getText().length() == 3) {
					int oldVal = Integer.parseInt(spin.getEditor().getText());
					spin.getValueFactory().setValue(200);
					max = Integer.parseInt(spin.getEditor().getText());
					spin.getValueFactory().setValue(oldVal);
					if(Integer.parseInt(spin.getEditor().getText()) > max) {
						spin.getValueFactory().setValue(max);
					}
				}
				slider.setValue(spin.getValue());
			}
		});
	}
	
	class keyHandler implements EventHandler<KeyEvent>{
		Spinner<Integer> spin = new Spinner<>();
		
		keyHandler(Spinner<Integer> spin){
			this.spin = spin;
		}
		@Override
		public void handle(KeyEvent event) {
			char c = event.getCharacter().charAt(0);
			if(!(Character.isDigit(c))) {
				event.consume();
			}
			if(spin.getEditor().getText().length() > 2 && !bypassed) {
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
}
