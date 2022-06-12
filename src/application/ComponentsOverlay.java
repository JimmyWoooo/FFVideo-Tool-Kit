package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileSystemView;

import components.sep;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ComponentsOverlay {
	private Boolean bypassed = false;
	private Boolean tabPress = false;
	private File file;
	private StackPane stack;
	private String prevPath;
	private TextField imagePath;
	private TextField gifPath;
	private String id = "image";
	private RadioButton trRadio;
	private RadioButton tlRadio;
	private RadioButton brRadio;
	private RadioButton blRadio;
	private RadioButton imageRadio;
	private RadioButton gifRadio;
	public CheckBox rotateBox;
	private Spinner<Integer> spin;
	
	CheckBox scaleBox;
	private RadioButton twice;
	private RadioButton customM;
	private TextField customMT;
	private double width;
	
	public BorderPane overlayPane(VideoDetails vid) {
		BorderPane border = new BorderPane();
		imageRadio = new RadioButton("Image: ");
		imagePath = new TextField("");
		gifRadio = new RadioButton("GIF: ");
		gifPath = new TextField("");
		Button openImage = new Button("Open");
		Button openGIF = new Button("Open");
		ToggleGroup paths = new ToggleGroup();
		VBox vboxLabel = new VBox(15);
		VBox vboxText = new VBox(15);
		VBox vboxButton = new VBox(15);
		
		imageRadio.setSelected(true);
		imageRadio.setToggleGroup(paths);
		gifRadio.setToggleGroup(paths);
		gifPath.setDisable(true);
		openGIF.setDisable(true);
		imagePath.setEditable(false);
		gifPath.setEditable(false);
		
		vboxLabel.getChildren().addAll(imageRadio, gifRadio);
		vboxLabel.setAlignment(Pos.TOP_LEFT);
		vboxText.getChildren().addAll(imagePath, gifPath);
		vboxText.setAlignment(Pos.TOP_CENTER);
		vboxButton.getChildren().addAll(openImage, openGIF);
		vboxText.setAlignment(Pos.TOP_LEFT);
		
		imageRadio.setOnAction(e->{
			openImage.setDisable(false);
			openGIF.setDisable(true);
			imagePath.setId("");
			imagePath.setDisable(false);
			gifPath.setId("strikethrough");
			gifPath.setDisable(true);
			id = "image";
		});
		gifRadio.setOnAction(e->{
			openGIF.setDisable(false);
			openImage.setDisable(true);
			gifPath.setId("");
			gifPath.setDisable(false);
			imagePath.setId("strikethrough");
			imagePath.setDisable(true);
			id = "gif";
		});
		
		border.setId("overlay");
		imagePath.setStyle("-fx-max-width: 420;");
		gifPath.setStyle("-fx-max-width: 420;");
		openImage.setOnAction(e -> openFile());
		openGIF.setOnAction(e -> openFile());
		
		BorderPane b = new BorderPane();
		b.setCenter(vboxText);
		b.setLeft(vboxLabel);
		b.setRight(vboxButton);
		b.setBottom(positionPane());
		border.setTop(b);
		border.setCenter(rotatePane());
		
		border.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if(event.getDragboard().hasFiles()) {
					event.acceptTransferModes(TransferMode.LINK);
				}
			}
		});
		border.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if(event.getDragboard().hasFiles()) {
					File file = event.getDragboard().getFiles().get(0);
					validateFile(file);
				}
			}
		});
		
		return border;
	}

	public BorderPane positionPane() {
		BorderPane border = new BorderPane();
		ToggleGroup toggle = new ToggleGroup();
		trRadio = new RadioButton("Top Right");
		tlRadio = new RadioButton("Top Left");
		brRadio = new RadioButton("Bottom Right");
		blRadio = new RadioButton("Bottom Left");
		Label position = new Label("Position: ");
		VBox topRadio = new VBox(5);
		VBox botRadio = new VBox(5);
		HBox hbox = new HBox(25);
		VBox vbox = new VBox(5);
		
		tlRadio.setSelected(true);
		trRadio.setToggleGroup(toggle);
		tlRadio.setToggleGroup(toggle);
		brRadio.setToggleGroup(toggle);
		blRadio.setToggleGroup(toggle);
		
		topRadio.getChildren().addAll(tlRadio, blRadio);
		botRadio.getChildren().addAll(trRadio, brRadio);
		hbox.getChildren().addAll(position, topRadio, botRadio);
		vbox.getChildren().addAll(new sep().getSep(), hbox, new sep().getSep());
		hbox.setAlignment(Pos.BASELINE_LEFT);
		border.setPadding(new Insets(10, 0, 0, 0));
		
		border.setTop(vbox);
		
		return border;
	}
	
	public BorderPane rotatePane() {
		BorderPane border = new BorderPane();
		rotateBox = new CheckBox();
		Label rotateLabel = new Label("Rotate: ");
		Rectangle rect = new Rectangle();
		Text text = new Text("Image/GIF");
		stack = new StackPane();
		VBox vbox = new VBox();
		HBox hbox = new HBox(40);
		HBox rotateScale = new HBox();
		HBox rotate = new HBox();
		spin = new Spinner<>(assignSpinners(0, 359, 0));
		
		rect.setFill(Color.web("#8a8a8a"));
		hbox.setDisable(true);
		rotateBox.setOnAction(e -> {
			if(!hbox.isDisabled()) 
				rect.setFill(Color.web("#8a8a8a"));
			else
				rect.setFill(Color.WHITE);
			hbox.setDisable(!hbox.isDisabled());
		});
		
		stack.getChildren().addAll(rect, text);
		hbox.getChildren().addAll(stack, spin);
		rotate.getChildren().addAll(rotateBox, rotateLabel);
		vbox.getChildren().addAll(rotate, hbox);
		rotateScale.getChildren().addAll(vbox, scalePane());
		assignEvents(spin, stack);
		spin.setMinWidth(50);
		
		rect.setWidth(100);
		rect.setHeight(125);
		hbox.setPadding(new Insets(10, 0, 0, 30));
		
		border.setTop(rotateScale);
		border.setCenter(new sep().getSep());
		border.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
		
		return border;
	}
	
	public BorderPane scalePane() {
		BorderPane border = new BorderPane();
		scaleBox = new CheckBox("Scale: ");
		twice = new RadioButton("2x");
		customM = new RadioButton("Custom Multiplier: ");
		customMT = new TextField("");
		ToggleGroup toggle = new ToggleGroup();
		VBox vbox = new VBox(5);
		HBox customMHbox = new HBox(5);
		HBox customIHbox = new HBox(5);
		VBox customVbox = new VBox(5);
		VBox whVbox = new VBox(5);
		VBox preserveV = new VBox(7);
		
		twice.setSelected(true);
		customMT.setDisable(true);
		twice.setToggleGroup(toggle);
		customM.setToggleGroup(toggle);
		customVbox.setDisable(true);
		scaleBox.setOnAction(e -> customVbox.setDisable(!customVbox.isDisabled()));
		customMT.setPrefWidth(40);
		customIHbox.setPadding(new Insets(0, 0, 0, 20));
		
		assignRadios(customM, customMT);
		
		customMHbox.getChildren().addAll(customM, customMT);
		customIHbox.getChildren().addAll(whVbox, preserveV);
		customVbox.getChildren().addAll(twice, customMHbox, customIHbox);
		vbox.getChildren().addAll(scaleBox, customVbox);
		border.setPadding(new Insets(0, 0, 0, 15));
		
		border.setTop(vbox);
		
		return border;
	}
	
	public void openFile() {
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
			validateFile(file);
		}
		else if(oldFile != null){
			file = new File(oldFile);
		}
	}
	
	public void validateFile(File file) {
		try {
			Path paths = Paths.get(file.getAbsolutePath());
			String content = Files.probeContentType(paths);
			String type[] = {""};
			if(id == "image") {
				if(content != null) {
					type = content.split("/");
				}
				if(ImageIO.read(file) == null || (!type[1].equals("jpeg") && !type[1].equals("png"))) {
					new AlertBox().display("Not an image.");
				}
				else {
					imagePath.setText(file.getAbsolutePath());
				}
			}
			if(id == "gif") {
				ImageReader ir = ImageIO.getImageReadersBySuffix("gif").next();
				ImageInputStream iis = ImageIO.createImageInputStream(file);
				ir.setInput(iis);
				int images = ir.getNumImages(true);
				if(images < 1) {
					new AlertBox().display("Not a gif.");
				}
				else {
					gifPath.setText(file.getAbsolutePath());
				}
			}
			prevPath = file.getParent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRadioSelected() {
		if(trRadio.isSelected()) {
			return "overlay=x=(main_w-overlay_w):y=0";
		}
		else if(tlRadio.isSelected()) {
			return "overlay=x=0:y=0";
		}
		else if(brRadio.isSelected()) {
			return "overlay=x=(main_w-overlay_w):y=(main_h-overlay_h)";
		}
		else
			return "overlay=x=0:y=main_h-overlay_h";
	}
	
	public boolean isRotated() {
		return rotateBox.isSelected();
	}
	
	public boolean isScaled() {
		return scaleBox.isSelected();
	}
	
	public String getValues() {
		if(twice.isSelected()) {
			return "\"iw*2:ih*2\"";
		}
		if(customM.isSelected()) {
			if(customMT != null) {
				width = Double.parseDouble(customMT.getText());
			}
			return "\"iw*" + width + ":ih*" + width + "\"";
		}
		return "\"iw*1:ih*1\"";
	}
	
	public boolean validInput() {
		if(customM.isSelected() && customMT == null) {
			return false;
		}
		return true;
	}
	
	public String getPath() {
		if(imageRadio.isSelected()) {
			return imagePath.getText();
		}else {
			return gifPath.getText();
		}
	}
	
	public boolean pathExists() {
		if(imageRadio.isSelected()) {
			return new File(imagePath.getText()).exists();
		}else {
			return new File(gifPath.getText()).exists();
		}
	}
	
	public String getId() {
		return id;
	}
	
	public int getDegrees() {
		return spin.getValue();
	}
	
	public void assignRadios(RadioButton radio, TextField t) {
		radio.selectedProperty().addListener((observable, oldVal, newValue) ->{
			if(newValue) {
				t.setDisable(false);
			}
			else {
				t.setDisable(true);
			}
		});
	}
	
	public void assignRadios(RadioButton radio, TextField t, CheckBox c) {
		radio.selectedProperty().addListener((observable, oldVal, newValue) ->{
			if(newValue) {
				c.setDisable(false);
				t.setDisable(false);
			}
			else {
				c.setDisable(true);
				t.setDisable(true);
			}
		});
	}
	
	public SpinnerValueFactory.IntegerSpinnerValueFactory assignSpinners(int min, int max, int start){
		SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, start);
		spinnerValue.setWrapAround(true);
		return spinnerValue;
	}
	
	public void assignEvents(Spinner<Integer> spin, StackPane stack) {
		spin.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.getDeltaY() > 0) {
					spin.increment();
					stack.setRotate(spin.getValue());
					if(!spin.getEditor().isFocused()) {
						spin.getEditor().requestFocus();
						bypassed = true;
					}
				}
				else {
					spin.decrement();
					stack.setRotate(spin.getValue());
					if(!spin.getEditor().isFocused()) {
						spin.getEditor().requestFocus();
						bypassed = true;
					}
				}
				spin.getEditor().selectAll();
			}
		});
		spin.getEditor().addEventFilter(KeyEvent.KEY_TYPED, new keyHandler(spin));
		spin.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.TAB)
					tabPress = true;
			}
		});
		spin.getEditor().setOnMouseClicked(e -> {
			spin.getEditor().selectAll();
			bypassed = true;
		});
		spin.setEditable(true);
		spin.getEditor().end();
		spin.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				Platform.runLater(spin.getEditor()::selectAll);
				bypassed = true;
            }
			if(oldValue) {
				if(spin.getEditor().getText().length() == 0) {
					spin.getValueFactory().setValue(1);
					spin.decrement();
				}
				if(spin.getEditor().getText().length() == 3) {
					int oldVal = Integer.parseInt(spin.getEditor().getText());
					if(oldVal > 359) {
						spin.getValueFactory().setValue(359);
						stack.setRotate(359);
					}
				}
				int num = Integer.parseInt(spin.getEditor().getText());
				spin.getValueFactory().setValue(num);
				stack.setRotate(num);
			}
		});
	}
	
	class keyHandler implements EventHandler<KeyEvent>{
		Spinner<Integer> spin = new Spinner<>();
		
		keyHandler(Spinner<Integer> spin){
			this.spin = spin;
		}
		@Override
		public void handle(KeyEvent event) {
			char c = event.getCharacter().charAt(0);
			if(!(Character.isDigit(c))) {
				event.consume();
			}
			if(spin.getEditor().getText().length() > 2 && !bypassed) {
				event.consume();
			}
			if(tabPress) {
				bypassed = true;
				tabPress = false;
			}
			else {
				bypassed = false;
			}
			stack.setRotate(spin.getValue());
		}
	}
}
