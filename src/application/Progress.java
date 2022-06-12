package application;

import java.math.BigDecimal;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Progress {
	BorderPane root;
	private Label progress;
	private ProgressBar pb;
	private Process process;
	boolean cancelled = false;
	
	Progress(BorderPane root) {
		this.root = root;
	}

	public void createPB() {
		progress = new Label("0%");
		progress.setId("progress");
		VBox vbox = new VBox();
		HBox hbox = new HBox();
		StackPane sp = new StackPane();
		StackPane c = new StackPane();
		
		Image closePic = new Image(getClass().getResourceAsStream("/close.png"));
		Image closeClickPic = new Image(getClass().getResourceAsStream("/closeClick.png"));
		ImageView close = new ImageView(closePic);
		close.setFitHeight(20);
		close.setFitWidth(20);
		c.setOnMousePressed(event -> {
			close.setImage(closeClickPic);
			System.out.println("cancelled");
			cancelProcess();
		});
		c.setOnMouseReleased(event -> close.setImage(closePic));
		
		pb = new ProgressBar(0);

		sp.getChildren().addAll(pb, progress);
		sp.setPadding(new Insets(0, 10, 0, 10));
		c.getChildren().add(close);
		hbox.setPadding(new Insets(0, 10, 0, 0));
		
		hbox.getChildren().addAll(c);
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		vbox.getChildren().addAll(sp, hbox);
		root.setBottom(vbox);
	}
	
	public void setProgress(double val) {
		pb.setProgress(val);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				BigDecimal prog = new BigDecimal(String.format("%.2f", val * 100));
				if(prog.doubleValue() >= 100) {
					progress.setText("100%");
				}else {
					progress.setText(prog.doubleValue()+ "%");
				}
			}
		});
	}
	
	public void setProcess(Process p) {
		process = p;
		cancelled = false;
	}
	
	public void cancelProcess() {
		if(process != null && process.isAlive()) {
			process.destroy();
			cancelled = true;
		}
	}
}
