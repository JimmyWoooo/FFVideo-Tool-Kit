package components;

import handlers.scrollHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class timeStamps {
	Spinner<Integer> startHour = new Spinner<>(assignSpinners(0, 23, 0));
	Spinner<Integer> startMinute = new Spinner<>(assignSpinners(0, 59, 0));
	Spinner<Integer> startSecond = new Spinner<>(assignSpinners( 0, 59, 0));
	Spinner<Integer> endHour = new Spinner<>(assignSpinners(0, 23, 0));
	Spinner<Integer> endMinute = new Spinner<>(assignSpinners(0, 59, 0));
	Spinner<Integer> endSecond = new Spinner<>(assignSpinners(0, 59, 0));
	Boolean bypassed = false;
	Boolean tabPress = false;
	private CheckBox startC, endC;
	int max;

	public BorderPane timeStampsLayout() {
		BorderPane border = new BorderPane();
		startC = new CheckBox();
		endC = new CheckBox();
		VBox startV = new VBox();
		VBox endV = new VBox();
		VBox vbox = new VBox();
		HBox startH = new HBox(5);
		HBox endH = new HBox(5);
		HBox startF = new HBox();
		HBox endF = new HBox();
		Label startLabel = new Label("Start:");
		Label endLabel = new Label("End:");
		assignEvents(startHour);
		assignEvents(startMinute);
		assignEvents(startSecond);
		assignEvents(endHour);
		assignEvents(endMinute);
		assignEvents(endSecond);
		
		startC.setOnMouseClicked(e -> disableStartSpinners(startC));
		endC.setOnMouseClicked(e -> disableEndSpinners(endC));
		
		vbox.setPadding(new Insets(0, 0, 10, 0));
		vbox.setSpacing(10);
		
		startH.getChildren().addAll(startHour, new colonLabels().getColon(), startMinute, new colonLabels().getColon(), startSecond);
		startV.getChildren().addAll(startLabel, startH);
		startF.getChildren().addAll(startC, startV);
		startF.setAlignment(Pos.BOTTOM_LEFT);
		startC.setPadding(new Insets(0, 5, 10, 0));
		endH.getChildren().addAll(endHour, new colonLabels().getColon(), endMinute, new colonLabels().getColon(), endSecond);
		endV.getChildren().addAll(endLabel, endH);
		endF.setAlignment(Pos.BOTTOM_LEFT);
		endC.setPadding(new Insets(0, 5, 10, 0));
		endF.getChildren().addAll(endC, endV);
		vbox.getChildren().addAll(startF, endF);
		border.setCenter(vbox);
		
		vbox.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
		
		return border;
	}
	
	public String getStart() {
		String hour = startHour.getEditor().getText();
		String min = startMinute.getEditor().getText();
		String sec = startSecond.getEditor().getText();
		
		return hour + ":" + min + ":" + sec;
	}
	
	public String getEnd() {
		String hour = endHour.getEditor().getText();
		String min = endMinute.getEditor().getText();
		String sec = endSecond.getEditor().getText();
		
		return hour + ":" + min + ":" + sec;
	}
	
	public boolean getStartCheck() {
		if(startC.isSelected()) {
			return true;
		}
		else
			return false;
	}
	
	public boolean getEndCheck() {
		if(endC.isSelected()) {
			return true;
		}
		else
			return false;
	}
	
	public void disableStartSpinners(CheckBox box) {
		if(!box.isSelected()) {
			startHour.setDisable(true);
			startMinute.setDisable(true);
			startSecond.setDisable(true);
		}
		else {
			startHour.setDisable(false);
			startMinute.setDisable(false);
			startSecond.setDisable(false);
		}
	}
	
	public void disableEndSpinners(CheckBox box) {
		if(!box.isSelected()) {
			endHour.setDisable(true);
			endMinute.setDisable(true);
			endSecond.setDisable(true);
		}
		else {
			endHour.setDisable(false);
			endMinute.setDisable(false);
			endSecond.setDisable(false);
		}
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
			if(spin.getEditor().getText().length() > 1 && !bypassed) {
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
	
	public SpinnerValueFactory.IntegerSpinnerValueFactory assignSpinners(int min, int max, int start){
		SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, start);
		spinnerValue.setWrapAround(true);
		return spinnerValue;
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
		spin.setDisable(true);
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
				if(spin.getEditor().getText().length() == 2) {
					int oldVal = Integer.parseInt(spin.getEditor().getText());
					spin.getValueFactory().setValue(100);
					max = Integer.parseInt(spin.getEditor().getText());
					spin.getValueFactory().setValue(oldVal);
					if(Integer.parseInt(spin.getEditor().getText()) > max) {
						spin.getValueFactory().setValue(max);
					}
				}
				setZeros(spin);
			}
		});
		setZeros(spin);
	}
	
	public void setZeros(Spinner<Integer> s) {
		int time = Integer.parseInt(s.getEditor().getText());
		if(time < 10) {
			s.getEditor().setText("0" + time);
		}
	}
	
	public String getTimeDiff(int totalTime) {
		int start = 0;
		int end = 0;
		int diff = 0;
		if(startC.isSelected()) {
			int hour = Integer.parseInt(startHour.getEditor().getText()) * 3600;
			int min = Integer.parseInt(startMinute.getEditor().getText()) * 60;
			int sec = Integer.parseInt(startSecond.getEditor().getText());
			start = hour + min + sec;
		}
		if(endC.isSelected()) {
			int hour = Integer.parseInt(endHour.getEditor().getText()) * 3600;
			int min = Integer.parseInt(endMinute.getEditor().getText()) * 60;
			int sec = Integer.parseInt(endSecond.getEditor().getText());
			end = hour + min + sec;
		}
		if(end != 0) {
			diff = end - start;
		}
		else {
			diff = totalTime - start;
		}
		int hour = diff / 3600;
		int min = (diff / 60) % 60;
		int sec = diff % 60; 
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}
}
