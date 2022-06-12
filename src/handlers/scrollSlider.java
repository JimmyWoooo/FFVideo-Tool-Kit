package handlers;

import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;

public class scrollSlider implements EventHandler<ScrollEvent>{
	Slider slider = null;
	
	public scrollSlider(Slider slider){
		this.slider = slider;
	}
	@Override
	public void handle(ScrollEvent event) {
		if(event.getDeltaY() > 0) {
			slider.increment();
		}
		else {
			slider.decrement();
		}
	}
}