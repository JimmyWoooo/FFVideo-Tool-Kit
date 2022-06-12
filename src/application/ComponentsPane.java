package application;

import components.fileOutput;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ComponentsPane {
	private String[] vidCodecs = {"H.264", "Copy",  "H.265", "MPEG-4", "VP9"};
	private String[] audCodecs = {"AAC", "Copy", "FLAC", "MP3", "OPUS", "None"};
	CheckBox copyBox;
	Button trim, crop, merge, overlay, decimate, m3u8;
	BorderPane current;
	ComboBox<String> ac, vc;
	VideoDetails vid;
	fileOutput fileOut;
	Output out;
	
	public ComponentsPane(VideoDetails vid, Output out) {
		this.vid = vid;
		this.out = out;
	}
	
	public void createPanel(BorderPane root, Progress pb) {
		BorderPane border = new BorderPane();
		HBox buttons = createHbox();
		BorderPane stack = createStack(pb);
		border.setTop(buttons);
		border.setCenter(stack);
		root.setLeft(border);
	}
	
	public HBox createHbox() {
		HBox hbox = new HBox();
		hbox.setId("componentHbox");
		hbox.setPrefWidth(620);
		trim = new Button("Trim");
		crop = new Button("Crop");
		merge = new Button("Merge");
		overlay = new Button("Overlay");
		decimate = new Button("Decimate");
		m3u8 = new Button("M3U8");
		
		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.getChildren().addAll(trim, crop, merge, overlay, decimate, m3u8);
		return hbox;
	}
	
	public BorderPane createStack(Progress pb) {
		BorderPane border = new BorderPane();
		StackPane stack = new StackPane();
		fileOut = new fileOutput();
		border.setId("stack");
		border.setPrefSize(400, 600);
		stack.setPrefSize(400, 100);
		
		HBox hbox = new HBox(10);
		VBox vbox = new VBox();
		vc = new ComboBox<>();
		Label vidCodec = new Label("Video Codec: ");
		ac = new ComboBox<>();
		Label audCodec = new Label("Audio Codec: ");
		Label copy = new Label("Copy Both: ");
		copyBox = new CheckBox();
		
		vc.getItems().addAll(vidCodecs);
		vc.getSelectionModel().selectFirst();
		ac.getItems().addAll(audCodecs);
		ac.getSelectionModel().selectFirst();
		copyBox.setOnAction(e -> {
			vc.setDisable(!vc.isDisabled());
			ac.setDisable(!ac.isDisabled());
		});
		
		ComponentsTrim tp = new ComponentsTrim();
		BorderPane trimPane = tp.trimPane(vid);
		ComponentsMerge mp = new ComponentsMerge();
		BorderPane mergePane = mp.mergePane(vid);
		ComponentsCrop cp = new ComponentsCrop();
		BorderPane cropPane = cp.cropPane(vid);
		ComponentsDecimate cd = new ComponentsDecimate();
		BorderPane decimatePane = cd.decimatePane(vid);
		ComponentsM3U8 m3u8p = new ComponentsM3U8();
		BorderPane m3u8Pane = m3u8p.m3u8Pane(vid);
		ComponentsOverlay co = new ComponentsOverlay();
		BorderPane overlayPane = co.overlayPane(vid);
		
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		hbox.getChildren().addAll(vidCodec, vc, audCodec, ac, copy, copyBox);
		hbox.setPadding(new Insets(0, 0, 10, 10));
		vbox.getChildren().addAll(stack, fileOut.fileOutputLayout(vid, pb));
		vbox.setPadding(new Insets(30, 30, 20, 30));
		border.setBottom(hbox);
		stack.getChildren().addAll(trimPane,cropPane, mergePane, overlayPane, decimatePane, m3u8Pane);
		border.setTop(vbox);
		current = trimPane;
		mergePane.setVisible(false);
		cropPane.setVisible(false);
		decimatePane.setVisible(false);
		m3u8Pane.setVisible(false);
		overlayPane.setVisible(false);
		
		fileOut.setCodecs(ac, vc, copyBox);
		fileOut.setTrim(tp.vol, tp.scale, tp.time);
		fileOut.setOut(this.out);
		trim.setOnAction(e -> switchTrim(trimPane, tp));
		crop.setOnAction(e -> switchCrop(cropPane, cp));
		merge.setOnAction(e -> switchMerge(mergePane, mp));
		decimate.setOnAction(e -> switchDecimate(decimatePane, cd));
		m3u8.setOnAction(e -> switchM3U8(m3u8Pane, m3u8p));
		overlay.setOnAction(e -> switchOverlay(overlayPane, co));
		
		return border;
	}
	
	public void switchTrim(BorderPane border,  ComponentsTrim tp) {
		switchPane(border, "trim");
		fileOut.setTrim(tp.vol, tp.scale, tp.time);
	}
	
	public void switchMerge(BorderPane border, ComponentsMerge mp) {
		switchPane(border, "merge");
		fileOut.setMerge(mp);
	}
	
	public void switchCrop(BorderPane border, ComponentsCrop cp) {
		switchPane(border, "crop");
		fileOut.setCrop(cp);
	}
	
	public void switchOverlay(BorderPane border, ComponentsOverlay co) {
		switchPane(border, "overlay");
		fileOut.setOverlay(co);
	}
	
	public void switchDecimate(BorderPane border, ComponentsDecimate cd) {
		switchPane(border, "decimate");
		fileOut.setDecimate(cd);
	}
	
	public void switchM3U8(BorderPane border, ComponentsM3U8 m3u8p) {
		switchPane(border, "m3u8");
		fileOut.setM3U8(m3u8p);
	}
	
	public void switchPane(BorderPane border, String id) {
		if(id == "merge" || id == "decimate" || id == "m3u8") {
			copyBox.setDisable(true);
			vc.setDisable(true);
			ac.setDisable(true);
		}
		else if(id == "crop" || id == "overlay") {
			copyBox.setDisable(true);
			vc.setDisable(false);
			ac.setDisable(false);
		}
		else {
			copyBox.setDisable(false);
			vc.setDisable(false);
			ac.setDisable(false);
		}

		if(copyBox.isSelected()) {
			vc.setDisable(true);
			ac.setDisable(true);
		}
		current.setVisible(false);
		border.setVisible(true);
		current = border;
	}
}
