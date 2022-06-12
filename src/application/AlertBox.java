package application;

import java.io.File;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertBox {
	
	public void display(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.getDialogPane().setPrefSize(300, 100);
		
		alert.showAndWait();
	}
	
	public void displayConfirm(File outputPath) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Confirm Save As");
		alert.setHeaderText(null);
		alert.setContentText(outputPath.getName() + " already exists. Do you want to replace it?");

		alert.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.getDialogPane().getButtonTypes().remove(ButtonType.OK);
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.YES) {
			outputPath.delete();
		}
	}
	
	public void displayFileNoLongerExists(File inputPath) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("File No Longer Exists");
		alert.setHeaderText(null);
		alert.setContentText("Input file " + inputPath.getName() + " no longer exists.");

		alert.showAndWait();
		
	}
}
