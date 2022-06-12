package handlers;

import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.ScrollEvent;

public class scrollHandler implements EventHandler<ScrollEvent>{
	Spinner<Integer> spin;
	Slider slider = null;
	Boolean bypassed;
	
	public scrollHandler(Spinner<Integer> spin, Boolean bypassed){
		this.spin = spin;
		this.bypassed = bypassed;
	}
	
	public scrollHandler(Spinner<Integer> spin, Slider slider){
		this.spin = spin;
		this.slider = slider;
	}
	
	@Override
	public void handle(ScrollEvent event) {
		if(event.getDeltaY() > 0) {
			spin.increment();
			if(!spin.getEditor().isFocused()) {
				spin.getEditor().requestFocus();
				bypassed = true;
			}
			if(slider != null) {
				int val = spin.getValue();
				slider.setValue(val);
			}
		}
		else {
			spin.decrement();
			if(!spin.getEditor().isFocused()) {
				spin.getEditor().requestFocus();
				bypassed = true;
			}
			if(slider != null) {
				int val = spin.getValue();
				slider.setValue(val);
			}
		}
		setZeros(spin);
		spin.getEditor().selectAll();
	}
	
	public void setZeros(Spinner<Integer> s) {
		int time = Integer.parseInt(s.getEditor().getText());
		if(time < 10) {
			s.getEditor().setText("0" + time);
		}
	}
}