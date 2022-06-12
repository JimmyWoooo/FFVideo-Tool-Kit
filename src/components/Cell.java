package components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Cell extends ListCell<String>{
	Label title = new Label("");
	BorderPane border = new BorderPane();
	Image closePic = new Image(getClass().getResourceAsStream("/close.png"));
	Image closeClickPic = new Image(getClass().getResourceAsStream("/closeClick.png"));
	ImageView close = new ImageView(closePic);
	StackPane c = new StackPane();
	
	public Cell(ListView<String> filePath, RadioButton swap) {
		super();
		c.getChildren().add(close);
		close.setFitHeight(20);
		close.setFitWidth(20);
		c.setOnMousePressed(event -> close.setImage(closeClickPic));
		c.setOnMouseReleased(event -> {
			close.setImage(closePic);
			int index = getIndex();
			getListView().getItems().remove(getItem());
			filePath.getItems().remove(index);
		});
		border.setLeft(title);
		border.setCenter(new Label(" "));
		border.setRight(c);
		border.prefWidthProperty().bind(super.widthProperty().subtract(50));
		setOnDragDetected(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
				ObservableList<String> items = getListView().getItems();
				int source = items.indexOf(getItem());
				try {
			        Dragboard dragboard = border.startDragAndDrop(TransferMode.MOVE);
			        ClipboardContent clipboardContent = new ClipboardContent();
			        clipboardContent.putString(items.get(source));
			        dragboard.setContent(clipboardContent);
			        event.consume();
				}
				catch(Exception e){
			        event.consume();
				}
		    }
		}); 
		setOnDragOver(new EventHandler<DragEvent>() {
		    @Override
		    public void handle(DragEvent event) {
		        final Dragboard dragboard = event.getDragboard();
		        if (dragboard.hasString()) {
		            event.acceptTransferModes(TransferMode.MOVE);
		        }
		    }
		});
		setOnDragDropped(new EventHandler<DragEvent>() {
		    public void handle(final DragEvent event) {
		        Dragboard db = event.getDragboard();
		        boolean success = false;
		        if (db.hasString()) {
		        	ObservableList<String> items = getListView().getItems();
		        	ObservableList<String> paths = filePath.getItems();
					int fileSource = items.indexOf(db.getString());
					int fileTarget = items.indexOf(getItem());
					int pathSource = items.indexOf(db.getString());
					int pathTarget = items.indexOf(getItem());
					if(fileTarget == -1) {
						fileTarget = items.indexOf(items.get(items.size() -1));
						pathTarget = items.indexOf(items.get(items.size() -1));
					}
					
					String temp = paths.get(pathSource);
					if(swap.isSelected()) {
						items.set(fileSource, items.get(fileTarget));
						items.set(fileTarget, db.getString());
						paths.set(pathSource, paths.get(pathTarget));
						paths.set(pathTarget, temp);
			            List<String> copied = new ArrayList<>(getListView().getItems());
			            getListView().getItems().setAll(copied);
					}
					else {
						List<String> copied = new ArrayList<>(getListView().getItems());
						List<String> copiedPaths = new ArrayList<>(paths);
						if(fileSource < fileTarget) {
							Collections.rotate(copied.subList(fileSource, fileTarget + 1), -1);
							Collections.rotate(copiedPaths.subList(pathSource, pathTarget + 1), -1);
						}
						else {
							Collections.rotate(copied.subList(fileTarget, fileSource + 1), 1);
							Collections.rotate(copiedPaths.subList(pathTarget, pathSource + 1), 1);
						}
						paths.setAll(copiedPaths);
						getListView().getItems().setAll(copied);
					}
		            success = true;
		        }
		        event.setDropCompleted(success);
		    }
		});
	}
	
	public void updateItem(String name, boolean empty) {
		super.updateItem(name, empty);
		setText(null);
		setGraphic(null);
		if(name != null && !empty) {
			title.setText(name);
			setGraphic(border);
		}
	}
}