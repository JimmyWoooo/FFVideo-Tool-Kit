package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.commons.io.FilenameUtils;


public class VideoDetails {
	private String tempDir = System.getProperty("java.io.tmpdir");
	private Image thumbnail;
	private ImageView imageView;
	private Label tDate, tName, tSize, tType, tDuration, tRes, tVideo, tAudio;
	private TextField loc;
	private String prevPath;
	File pic;
	String [] bytes = {"Bytes", "KB", "MB", "GB", "TB"};
	int count = 0;
	FFProbe ffprobe = new FFProbe("ffprobe.exe");
	private File file;
	private int totalTime, width, height;
	Output o;
	boolean sucess = true;
	
	public VideoDetails(){
		
	}
	
	public void fetchFile() throws Exception{
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
		file = fc.showOpenDialog(new Stage());
		if(file != null) {
			pic = new File(tempDir + "output.png");
			if(pic.exists()) {
				pic.delete();
				imageView.setImage(null);
			}
			getVideoInfo(file);
			prevPath = file.getParent();
		}else if(oldFile != null){
			file = new File(oldFile);
		}
	}
	
	public void createVidInfo(BorderPane root) {
		BorderPane border = createInfo();
		root.setRight(border);
	}
	
	public BorderPane createInfo() {
		BorderPane border = new BorderPane();
		BorderPane info = new BorderPane();
		BorderPane space = new BorderPane();
		BorderPane gT = generateThumbnail();
		ScrollPane sp = new ScrollPane();
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		sp.setStyle("-fx-background: #202020; -fx-background-color: transparent;");
		o = new Output();
		
		border.setId("border");
		space.setId("space");
		sp.setId("scrollPane");
		
		VBox gB = generateButton();
		VBox gL = generateLabels();
		VBox gText = generateText();
		
		border.setTop(gT);
		border.setCenter(sp);
		border.setBottom(gB);
		
		info.setLeft(gL);
		info.setCenter(gText);
		sp.setContent(info);
		
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
				file = e.getDragboard().getFiles().get(0);
				pic = new File(tempDir + "output.png");
				if(pic.exists()) {
					pic.delete();
					imageView.setImage(null);
				}
				getVideoInfo(file);
			}
		});
		sp.setVbarPolicy(ScrollBarPolicy.NEVER);
		sp.setOnMouseEntered(e -> {
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        });
		sp.setOnMouseExited(e -> {
			sp.setVbarPolicy(ScrollBarPolicy.NEVER);
        });
		
		space.setTop(border);
		space.setBottom(o.createOutput());
		return space;
	}
	
	public BorderPane generateThumbnail() {
		BorderPane border = new BorderPane();
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 0, 10, 0));
		imageView = new ImageView();
		vbox.setId("thumbnail");
		VBox gLoc = generateLoc();

		vbox.getChildren().addAll(imageView);
		vbox.setAlignment(Pos.TOP_CENTER);
		border.setTop(vbox);
		border.setBottom(gLoc);
		return border;
	}
	
	public VBox generateLoc() {
		VBox vbox = new VBox();
		loc = new TextField("");
		loc.setEditable(false);
		loc.setId("loc");
		vbox.getChildren().addAll(loc);
		vbox.setAlignment(Pos.CENTER);
		return vbox;
	}
	
	public VBox generateButton() {
		VBox vbox = new VBox();
		Button open = new Button("Open");
		open.setId("open");
		
		open.setOnAction(e -> {
			try {
				fetchFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		vbox.getChildren().addAll(open);
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		return vbox;
	}
	
	public VBox generateLabels() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 5, 10));
		vbox.setId("detailsLabel");
		Label name = new Label("Name: ");
		Label date = new Label("Date: ");
		Label type = new Label("Type: ");
		Label size = new Label("File Size: ");
		Label duration = new Label("Duration: ");
		Label res = new Label("Resolution: ");
		Label vid = new Label("Video Codec: ");
		Label aud = new Label("Audio Codec: ");
		
		vbox.getChildren().addAll(name, date, type, size, duration, res, vid, aud);
		vbox.setAlignment(Pos.BOTTOM_LEFT);
		return vbox;
	}
	
	public VBox generateText() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 5, 10));
		vbox.setId("detailsText");
		tName = new Label("");
		tDate = new Label("");
		tType = new Label("");
		tSize = new Label("");
		tDuration = new Label("");
		tRes = new Label("");
		tVideo = new Label("");
		tAudio = new Label("");
		
		vbox.getChildren().addAll(tName, tDate, tType, tSize, tDuration, tRes, tVideo, tAudio);
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		return vbox;
	}
	
	public void getVideoInfo(File file) {
		sucess = true;
		try {
			Path paths = Paths.get(file.getAbsolutePath());
			String content = Files.probeContentType(paths);
			if(!content.split("\\/")[0].equals("video") && !(FilenameUtils.getExtension(file.getName()).toString().toUpperCase().equals("TS"))) {
				new AlertBox().display("File is not a video.");
				resetInfo();
				sucess = false;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(sucess) {
			try {
				FFMPEG ffmpeg = new FFMPEG();
				ffmpeg.getThumbnail(file.getAbsolutePath());
				ffprobe.getVideoInfo(file.getAbsolutePath());
				if(ffprobe.isSucess()) {
					ffprobe.getCodecs(file.getAbsolutePath());
					
					BasicFileAttributes att = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					Scanner sc = new Scanner(att.creationTime().toInstant().atZone(ZoneId.systemDefault()).toString());
					Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
					String date[] = sc.findWithinHorizon(pattern, 0).split("-");
					sc.close();
					
					String size = getFileSize(file);
					Timer timer = new Timer();
					timeTask tt = new timeTask();	
					timer.scheduleAtFixedRate(tt, 200, 100);
					
					tName.setText(file.getName());
					tDate.setText(date[1] + "-" + date[2] + "-" + date[0]);
					tType.setText(FilenameUtils.getExtension(file.getName()).toString().toUpperCase());
					tSize.setText(size + " "+ bytes[count]);
					tDuration.setText(ffprobe.getTime());
					tRes.setText(ffprobe.getWidth() + " x " + ffprobe.getHeight());
					loc.setText(file.getAbsolutePath());
					totalTime = ffprobe.getTotalTime();
					width = Integer.parseInt(ffprobe.getWidth());
					height = Integer.parseInt(ffprobe.getHeight());
					tVideo.setText(ffprobe.getVideoCodec());
					tAudio.setText(ffprobe.getAudioCodec());
				}
				else {
					new AlertBox().display("Could not parse video info.");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				new AlertBox().display(e.toString());
			}
		}
	}
	
	public void setM3U8Info(FFProbe f) {
		tName.setText("--");
		tDate.setText("--");
		tType.setText("--");
		tSize.setText("--");
		tDuration.setText(f.getM3U8TimeString());
		tRes.setText(f.getM3U8Res());
		loc.setText("");
		tVideo.setText(f.getM3U8Video());
		tAudio.setText(f.getM3U8Audio());
		imageView.setImage(null);
	}
	
	public void resetInfo() {
		tName.setText("");
		tDate.setText("");
		tType.setText("");
		tSize.setText("");
		tDuration.setText("");
		tRes.setText("");
		loc.setText("");
		tVideo.setText("");
		tAudio.setText("");
		imageView.setImage(null);
	}
	
	public File getFile() {
		return file;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getTimeString() {
		return ffprobe.getTime();
	}
	
	public String getFileSize(File file) {
		long sizeB = file.length();
		double sizeKb = sizeB/1024;
		double sizeMb = sizeKb/1024;
		double sizeGb = sizeMb/1024;
		
		if(sizeB < 1024) {
			count = 0;
			return String.format("%.2f", sizeB);
		}
		else if(sizeKb < sizeB && sizeMb <= 1.0) {
			count = 1;
			return String.format("%.2f", sizeKb);
		}
		else if(sizeMb < sizeKb && sizeGb <= 1.0) {
			count = 2;
			return String.format("%.2f", sizeMb);
		}
		else 
			count = 3;
			return String.format("%.2f", sizeGb);
	}
	
	class timeTask extends TimerTask{
		public void run() {
			if(pic.exists()) {
				thumbnail = new Image(pic.toURI().toString());
				imageView.setImage(thumbnail);
				imageView.setFitWidth(330);
				imageView.setFitHeight(200);
				imageView.setPreserveRatio(true);
				this.cancel();
			}
		}
	}
}



