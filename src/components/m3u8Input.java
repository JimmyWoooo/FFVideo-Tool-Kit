package components;

import java.io.File;

import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FilenameUtils;

import application.AlertBox;
import application.FFProbe;
import application.VideoDetails;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class m3u8Input {
	private TextField localPath;
	private TextField urlPath;
	private File file;
	private String prevPath;
	private RadioButton localRadio;
	private RadioButton urlRadio;
	private FFProbe ffprobe;
	VideoDetails videoD;
	
	public BorderPane createM3U8(VideoDetails vid){
		videoD = vid;
		BorderPane border = new BorderPane();
		localRadio = new RadioButton("Local File: ");
		localPath = new TextField("");
		urlRadio = new RadioButton("URL: ");
		urlPath = new TextField("");
		Button open = new Button("Open");
		ToggleGroup paths = new ToggleGroup();
		VBox vboxLabel = new VBox(15);
		VBox vboxText = new VBox(15);
		HBox hbox = new HBox();
		ffprobe = new FFProbe("ffprobe.exe");
		
		localRadio.setSelected(true);
		localRadio.setToggleGroup(paths);
		urlRadio.setToggleGroup(paths);
		urlPath.setDisable(true);
		localPath.setStyle("-fx-max-width: 400;");
		urlPath.setStyle("-fx-max-width: 400;");
		open.setOnAction(e -> openFile(videoD));
		
		localRadio.setOnAction(e->{
			open.setDisable(false);
			localPath.setId("");
			localPath.setDisable(false);
			urlPath.setId("strikethrough");
			urlPath.setDisable(true);
		});
		urlRadio.setOnAction(e->{
			open.setDisable(true);
			urlPath.setId("");
			urlPath.setDisable(false);
			localPath.setId("strikethrough");
			localPath.setDisable(true);
		});
		
		vboxLabel.getChildren().addAll(localRadio, urlRadio);
		vboxLabel.setAlignment(Pos.CENTER_LEFT);
		vboxText.getChildren().addAll(localPath, urlPath);
		vboxText.setAlignment(Pos.BOTTOM_CENTER);
		hbox.getChildren().addAll(open);
		hbox.setAlignment(Pos.TOP_RIGHT);
		hbox.setPadding(new Insets(5, 0, 0, 0));
		
		BorderPane b = new BorderPane();
		b.setCenter(vboxText);
		b.setLeft(vboxLabel);
		b.setRight(hbox);
		border.setTop(b);
		
		border.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent e) {
				if(e.getDragboard().hasFiles()) {
					e.acceptTransferModes(TransferMode.LINK);
				}
			}
		});
		border.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent e) {
				File files = e.getDragboard().getFiles().get(0);
				if(validateFile(files)) {
					new AlertBox().display("File is not m3u8.");
					e.consume();
				}else {
					localPath.setText(files.getPath());
					try {
						ffprobe.getVideoTimeM3U8(files.getPath());
						vid.setM3U8Info(ffprobe);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		return border;
	}
	
	public void openFile(VideoDetails vid) {
		String oldFile = null;
		if(file != null) {
			oldFile = file.getAbsolutePath();
		}
		FileChooser fc = new FileChooser();
		if(prevPath == null) {
			fc.setInitialDirectory(new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()));	
		}else {
			fc.setInitialDirectory(new File(prevPath));
		}
		file  = fc.showOpenDialog(new Stage());
		if(file != null) {
			if(validateFile(file)) {
				new AlertBox().display("File is not m3u8.");
			}
			else {
				localPath.setText(file.getPath());
				try {
					ffprobe.getVideoTimeM3U8(file.getAbsolutePath());
					vid.setM3U8Info(ffprobe);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		else if(oldFile != null){
			file = new File(oldFile);
		}
	}
	
	public boolean validateFile(File file) {
		prevPath = file.getParent();
		if(FilenameUtils.getExtension(file.getName()).toString().toLowerCase().equals("m3u8")) {
			return false;
		}else {
			return true;
		}
	}
	
	public String getLocalPath() {
		return localPath.getText();
	}
	

	public String getUrlPath() {
		return urlPath.getText();
	}
	
	public boolean validateURL() {
		try {
			ffprobe.validateURL(urlPath.getText());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(ffprobe.isValidLink()) {
			try {
				ffprobe.getVideoTimeM3U8(urlPath.getText());
				videoD.setM3U8Info(ffprobe);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return ffprobe.isValidLink();
	}
	
	public boolean localRadio() {
		if(localRadio.isSelected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean urlRadio() {
		if(urlRadio.isSelected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public double getM3U8Time() {
		return ffprobe.getM3U8Time();
	}
}
