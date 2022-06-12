package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import components.Cell;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ComponentsMerge {
	private ListView<String> filePath;
	private String path;
	
	public BorderPane mergePane(VideoDetails vid) {
		BorderPane border = new BorderPane();
		ListView<String> lv = new ListView<>();
		filePath = new ListView<>();
		Button clearAll = new Button("Clear All");
		RadioButton swap = new RadioButton("Swap");
		RadioButton insert = new RadioButton("Insert");
		ToggleGroup toggle = new ToggleGroup();
		HBox hbox = new HBox(15);
		
		lv.setId("mergeLV");
		border.setId("merge");
		clearAll.setMinWidth(350);
		lv.setPrefHeight(390);
		swap.setToggleGroup(toggle);
		insert.setToggleGroup(toggle);
		swap.setSelected(true);
		
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
				List<File> files = e.getDragboard().getFiles();
				for(int x = 0; x < files.size(); x++) {
					lv.getItems().addAll(files.get(x).getName());
					filePath.getItems().addAll(files.get(x).getAbsolutePath());
					path = files.get(x).getParent();
				}
				lv.setCellFactory(param -> new Cell(filePath, swap));
			}
		});
		clearAll.setOnAction(e -> {
			lv.getItems().clear();
			filePath.getItems().clear();
		});
		
		hbox.getChildren().addAll(swap, insert, clearAll);
		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.setPadding(new Insets(10, 0, 0, 0));
		
		border.setTop(lv);
		border.setCenter(hbox);
		
		
		return border;
	}
	
	public ArrayList<String> getList(){
		ArrayList<String> list = new ArrayList<>(filePath.getItems());
		return list;
	}
	
	public String getPath() {
		return path;
	}
}
