package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application{
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			root.setId("root");
			Scene scene = new Scene(root,1000,800);
			scene.getStylesheets().add("application/stylesheet.css");
			BorderPane prog = new BorderPane();
			Progress pb = new Progress(prog);
			pb.createPB();
			root.setBottom(prog);
			VideoDetails vid = new VideoDetails();
			vid.createVidInfo(root);
			ComponentsPane c = new ComponentsPane(vid, vid.o);
			c.createPanel(root, pb);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> pb.cancelProcess());
			primaryStage.setResizable(false);
			primaryStage.setTitle("FFVideo Took-Kit");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

