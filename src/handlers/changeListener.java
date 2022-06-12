package handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

public class changeListener implements ChangeListener<Number>{
	Spinner<Integer> spin = new Spinner<>();
	Label ear;
	Boolean focus;
	public changeListener(Spinner<Integer> spin){
		this.spin = spin;
	}
	
	public changeListener(Spinner<Integer> spin, Label ear){
		this.spin = spin;
		this.ear = ear;
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		int val = newValue.intValue();
		spin.getEditor().setText(String.valueOf(val));
		if(val == 200) {
			ear.setVisible(true);
		}else {
			ear.setVisible(false);
		}
	}
}