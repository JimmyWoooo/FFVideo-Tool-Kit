package components;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import application.AlertBox;
import application.ComponentsCrop;
import application.ComponentsDecimate;
import application.ComponentsM3U8;
import application.ComponentsMerge;
import application.ComponentsOverlay;
import application.FFMPEG;
import application.Output;
import application.Progress;
import application.VideoDetails;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class fileOutput {
	private String[] vidExt = {"MP4", "AVI", "MKV", "MOV", "TS", "WEBM"};
	private TextField fileName;
	private ComboBox<String> cb; 
	private TextField outputPath;
	private volume v;
	private scaleOptions s;
	private timeStamps t;
	private VideoDetails vid;
	public ComponentsMerge merge;
	private String id;
	private int totalStart;
	private int totalEnd;
	private ComponentsCrop crop;
	private ComponentsOverlay overlay;
	private ComponentsDecimate decimate;
	private ComponentsM3U8 m3u8;
	public Progress pb;
	private Output outputInfo;
	public Button start; 
	private ComboBox<String> ac, vc;
	private CheckBox copyBox;

	public BorderPane fileOutputLayout(VideoDetails vid, Progress pb) {
		BorderPane border = new BorderPane();
		VBox outputBox = new VBox(5);
		HBox hbox = new HBox(5);
		VBox startBut = new VBox(5);
		Label outputLabel = new Label("Output Destination:");
		outputPath = new TextField(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
		Button dir = new Button("Choose directory");
		Button same = new Button("Same directory");
		start = new Button("Start");
		
		outputPath.setMinWidth(350);
		outputPath.setEditable(false);
		dir.setOnAction(e-> outputPath.setText(getPath(outputPath)));
		same.setOnAction(e -> {
			if(this.id == "merge" && this.merge.getPath() != null) {
				outputPath.setText(this.merge.getPath());
			}
			else if(vid.getFile() != null) {
				outputPath.setText(vid.getFile().getParent());
			}else {
				new AlertBox().display("No input file.");
			}
		});
		start.setMinWidth(350);
		
		hbox.getChildren().addAll(dir, same);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.setPadding(new Insets(0, 0, 0, 10));
		outputBox.getChildren().addAll(outputLabel, outputPath);
		outputBox.setPadding(new Insets(10, 0, 0, 0));
		startBut.getChildren().addAll(start);
		startBut.setAlignment(Pos.BOTTOM_CENTER);
		startBut.setPadding(new Insets(15, 0, 0, 0));
		
		setVar(vid, pb);
		start.setOnAction(e -> {
			if(vid.getFile() != null || id == "merge" || id == "decimate" || id == "m3u8") {
				try {
					startProgram(vid);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
			else {
				new AlertBox().display("No input file.");
			}
		});
		
		
		border.setLeft(outputBox);
		border.setRight(hbox);
		border.setTop(fileOutputName());
		border.setBottom(startBut);
		
		return border;
	}
	
	public BorderPane fileOutputName() {
		BorderPane border = new BorderPane();
		VBox vbox = new VBox(5);
		VBox vboxCB = new VBox();
		Label nameLabel = new Label("Output File Name:");
		fileName = new TextField("Output");
		cb = new ComboBox<>();
		cb.getItems().addAll(vidExt);
		cb.getSelectionModel().selectFirst();
		
		fileName.setId("fileName");
		vboxCB.getChildren().add(cb);
		vboxCB.setAlignment(Pos.BOTTOM_LEFT);
		vboxCB.setPadding(new Insets(0, 0, 0, 20));
		vbox.getChildren().addAll(nameLabel, fileName);
		border.setLeft(vbox);
		border.setCenter(vboxCB);
		
		return border;
	}
	
	public String getPath(TextField outputPath) {
		String path = "";
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(outputPath.getText()));
		File sd = dc.showDialog(new Stage());
		
		if(sd == null) {
			path = outputPath.getText();
		}
		else 
			path = sd.getAbsolutePath();
		
		return path;
	}
	
	public void setTrim(volume vol, scaleOptions scale, timeStamps time) {
		v = vol;
		s = scale;
		t = time;
		id = "trim";
	}
	
	public void setMerge(ComponentsMerge cm) {
		id = "merge";
		merge = cm;
	}
	
	public void setOut(Output out) {
		outputInfo = out;
	}
	
	public void setCrop(ComponentsCrop cp) {
		crop = cp;
		id = "crop";
		t = cp.getTime();
		v = cp.getVol();
	}
	
	public void setOverlay(ComponentsOverlay co) {
		overlay = co;
		id = "overlay";
	}
	

	public void setDecimate(ComponentsDecimate cd) {
		decimate = cd;
		id = "decimate";
		t = cd.getTimeStampsClass();
	}
	
	public void setM3U8(ComponentsM3U8 m3u8p) {
		id = "m3u8";
		m3u8 = m3u8p;
		t = m3u8.getTimeStampsClass();
		v = m3u8.getVol();
	}
	
	public void setCodecs(ComboBox<String> audio, ComboBox<String> video, CheckBox copy) {
		this.ac = audio;
		this.vc = video;
		this.copyBox = copy;
	}
	
	public void startProgram(VideoDetails vid) throws URISyntaxException {
		try {
			FFMPEG ffmpeg = new FFMPEG();
			ffmpeg.initialize(vid.getFile(), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getId() {
		return id;
	}
	
	public ComponentsOverlay getOverlay() {
		return overlay;
	}
	
	public String getOutputFileName() {
		return fileName.getText();
	}
	
	public String getExtension() {
		return "." + cb.getValue().toLowerCase();
	}
	
	public String getDest() {
		return outputPath.getText();
	}
	
	public double getVol() {
		if(v.normal.isSelected()) {
			return 100.0;
		}
		return v.slider.getValue();
	}
	
	public String getScale() {
		return s.cb.getValue();
	}
	
	public ComponentsCrop getCrop() {
		return crop;
	}
	
	public ComponentsDecimate getDec() {
		return decimate;
	}
	
	public VideoDetails getVidDetails() {
		return vid;
	}
	
	public Output getOutputInfo() {
		return outputInfo;
	}
	
	public timeStamps getTimeStamps() {
		return t;
	}
	
	public ComponentsM3U8 getM3U8() {
		return m3u8;
	}
	
	public String getStart() {
		String time = null;
		totalStart = 0;
		if(t.getStartCheck()) {
			int hour = Integer.parseInt(t.startHour.getEditor().getText());
			int min = Integer.parseInt(t.startMinute.getEditor().getText());
			int sec = Integer.parseInt(t.startSecond.getEditor().getText());
			totalStart = (hour * 3600) + (min * 60) + sec;
			time = String.format("%02d:%02d:%02d", hour, min, sec);
		}
		return time;
	}
	
	public String getEnd() {
		String time = null;
		totalEnd = 0;
		if(t.getEndCheck()) {
			int hour = Integer.parseInt(t.endHour.getEditor().getText());
			int min = Integer.parseInt(t.endMinute.getEditor().getText());
			int sec = Integer.parseInt(t.endSecond.getEditor().getText());
			totalEnd = (hour * 3600) + (min * 60) + sec;
			time = String.format("%02d:%02d:%02d", hour, min, sec);
		}
		return time;
	}
	
	public boolean checkTime() {
		if(t.getStartCheck()) {
			getStart();
			if(t.getEndCheck()) {
				getEnd();
				if(totalEnd < totalStart || vid.getTotalTime() < totalEnd || vid.getTotalTime() < totalStart) {
					return false;
				}
			}
			else {
				if(totalStart > vid.getTotalTime()) {
					return false;
				}
			}
		}
		if(t.getEndCheck()) {
			getEnd();
			if(totalEnd > vid.getTotalTime()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean checkDecimateM3U8Time() {
		if(t.getStartCheck()) {
			getStart();
			if(t.getEndCheck()) {
				getEnd();
				if(totalEnd < totalStart || decimate.getM3U8().getM3U8Time() < totalEnd || decimate.getM3U8().getM3U8Time() < totalStart) {
					return false;
				}
			}
			else {
				if(totalStart > decimate.getM3U8().getM3U8Time()) {
					return false;
				}
			}
		}
		if(t.getEndCheck()) {
			getEnd();
			if(totalEnd > decimate.getM3U8().getM3U8Time()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean checkM3U8Time() {
		if(t.getStartCheck()) {
			getStart();
			if(t.getEndCheck()) {
				getEnd();
				if(totalEnd < totalStart || m3u8.getM3U8().getM3U8Time() < totalEnd || m3u8.getM3U8().getM3U8Time() < totalStart) {
					return false;
				}
			}
			else {
				if(totalStart > m3u8.getM3U8().getM3U8Time()) {
					return false;
				}
			}
		}
		if(t.getEndCheck()) {
			getEnd();
			if(totalEnd > m3u8.getM3U8().getM3U8Time()) {
				return false;
			}
		}
		return true;
	}
	
	public int getTotalStart() {
		return totalStart;
	}
	
	public int getTotalEnd() {
		return totalEnd;
	}
	
	public void getAudioValue(List<String> command){
		if(ac.getValue() != "None") {
			command.add("-c:a");
			if(ac.getValue() == "AAC") {
				command.add("aac");
			}
			else if(ac.getValue() == "OPUS") {
				command.add("libopus");
			}
			else if(ac.getValue() == "Copy") {
				command.add("copy");
			}
			else if(ac.getValue() == "FLAC") {
				command.add("flac");
			}
			else if(ac.getValue() == "MP3") {
				command.add("libmp3lame");
			}
		}
		else{
			command.add("-an");
		}
	}
	
	public void getVideoValue(List<String> command){
		command.add("-c:v");
		if(vc.getValue() == "H.264") {
			command.add("libx264");
		}
		else if(vc.getValue() == "H.265") {
			command.add("libx265");
		}
		else if(vc.getValue() == "Copy") {
			command.add("copy");
		}
		else if(vc.getValue() == "MPEG-4") {
			command.add("mpeg4");
		}
		else if(vc.getValue() == "VP9") {
			command.add("libvpx-vp9");
		}
	}
	
	public String getVCB() {
		return vc.getValue();
	}
	
	public String getACB() {
		return ac.getValue();
	}
	
	public boolean getCopy() {
		if(copyBox.isSelected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setVar(VideoDetails vid, Progress pb) {
		this.vid = vid;
		this.pb = pb;
	}
}
