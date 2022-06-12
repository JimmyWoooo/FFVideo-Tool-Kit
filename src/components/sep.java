package components;

import javafx.geometry.Insets;
import javafx.scene.control.Separator;

public class sep {
	Separator separate = new Separator();

	public sep() {
		this.separate = createSep();
	}
	
	private Separator createSep() {
		Separator separate = new Separator();

		separate.setMaxWidth(550);
		separate.setPadding(new Insets(5, 15, 5, 15));
		
		return separate;
	}
	
	public Separator getSep() {
		return this.separate;
	}
}
