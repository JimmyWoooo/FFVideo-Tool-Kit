package components;

import javafx.scene.control.Label;

public class colonLabels {
	Label colon;
	
	
	public colonLabels() {
		this.colon = createColon();
	}
	
	private Label createColon() {
		Label colon = new Label(":");
		colon.setStyle("-fx-font-size: 1.6em");
		
		return colon;
	}
	
	public Label getColon() {
		return this.colon;
	}
}
