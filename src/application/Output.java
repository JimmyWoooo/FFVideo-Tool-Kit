package application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Output {
	private Label tFrame, tSize, tSpeed, tCurrent, tEnd, tElapased, tStatus, tTimeStarted, tTimeFinished, tRemaining;
	private String [] bytes = {"KB", "MB", "GB", "TB"};
	int count = 0;
	int miliSec = 0;
	int sec = 0;
	int min = 0;
	int hour = 0;
	Timer timer;
	TimerTask ts;
	Date date;
	double currTime = 0;
	
	public Output() {
		
	}
	
	public BorderPane createOutput() {
		BorderPane outputBorder = new BorderPane();
		outputBorder.setId("outputBorder");
		outputBorder.setPrefSize(350, 250);
		VBox gL = generateLabels();
		VBox gText = generateText();
		
		outputBorder.setLeft(gL);
		outputBorder.setCenter(gText);
		
		return outputBorder;
	}
	
	public VBox generateLabels() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 5, 10));
		vbox.setId("detailsLabel");
		Label current = new Label("Current: ");
		Label end = new Label("End: ");
		Label frame = new Label("Frames: ");
		Label speed = new Label("Speed: ");
		Label size = new Label("File Size: ");
		Label timeStarted = new Label("Time Started: ");
		Label timeFinished = new Label("Time Finished: ");
		Label elapased = new Label("Elapsed Time: ");
		Label remaining = new Label("Time Remaining: ");
		Label status = new Label("Current Status: ");
		
		vbox.getChildren().addAll(current, end, frame, speed, size, timeStarted, timeFinished, elapased, remaining, status);
		vbox.setAlignment(Pos.TOP_LEFT);
		return vbox;
	}
	
	public VBox generateText() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 5, 10));
		vbox.setId("detailsText");
		tCurrent = new Label("--:--:--");
		tEnd = new Label("--:--:--");
		tFrame = new Label("--");
		tSpeed = new Label("--");
		tSize = new Label("--");
		tTimeStarted = new Label("--:--:--");
		tTimeFinished = new Label("--:--:--");
		tElapased = new Label("--:--:--");
		tRemaining = new Label("--:--:--");
		tStatus= new Label("Idle");
		
		vbox.getChildren().addAll(tCurrent, tEnd, tFrame, tSpeed, tSize, tTimeStarted, tTimeFinished, tElapased, tRemaining, tStatus);
		vbox.setAlignment(Pos.TOP_CENTER);
		return vbox;
	}
	
	public void setInfo(String current, String frame, String speed, String size, double progress) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				double remain = 1.0/progress;
				double remainTime = remain * currTime;
				double time = remainTime - currTime;
				
				if(time > 0) {
					formatTimeString(time);
				}
				
				tCurrent.setText(current);
				tFrame.setText(frame);
				tSpeed.setText(speed + " seconds/sec");
				tSize.setText(getFileSize(size) + " " + bytes[count]);
			}
		});
	}
	
	public void setEnd(String end) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tEnd.setText(end);
			}
		});
	}
	
	public void setStatus(String status) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tStatus.setText(status);
			}
		});
	}
	
	public String getFileSize(String size) {
		int sizeKb = Integer.parseInt(size.trim());
		double sizeMb = sizeKb/1024.0;
		double sizeGb = sizeMb/1024.0;
		
		if(sizeKb < 1024 && sizeMb <= 1) {
			count = 0;
			return String.format("%d", sizeKb);
		}
		else if(sizeMb < sizeKb && sizeGb <= 1.0) {
			count = 1;
			return String.format("%.02f", sizeMb);
		}
		else 
			count = 2;
			return String.format("%.02f", sizeGb);
	}
	
	public void startTimer() {
		sec = 0;
		miliSec = 0;
		min = 0;
		hour = 0;
		currTime = 0;
		timer = new Timer();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				date = new Date();
				tTimeFinished.setText("--:--:--");
				tTimeStarted.setText(new SimpleDateFormat("hh:mm:ss").format(date));
			}
		});
		ts = new TimerTask() {
			@Override
			public void run() {
				miliSec++;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(miliSec > 1000) {
							miliSec = 0;
							sec++;
							currTime++;
						}
						if(sec > 59) {
							sec = 0;
							min++;
						}
						if(min > 59) {
							min = 0;
							hour++;
						}
						
						String timeFormat = String.format("%02d:%02d:%02d.%d", hour, min, sec, miliSec);
						tElapased.setText(timeFormat);
					}
				});
			}
		};
		timer.scheduleAtFixedRate(ts, 0, 1);
	}
	
	public void stopTimer() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				date = new Date();
				tTimeFinished.setText(new SimpleDateFormat("hh:mm:ss").format(date));
			}
		});
		timer.cancel();
	}
	
	public void formatTimeString(double time) {
		int totalTime = (int) Math.round(time);
		int hour = totalTime / 3600;
		int min = (totalTime / 60) % 60;
		int sec = totalTime % 60; 
		
		tRemaining.setText(String.format("%02d:%02d:%02d", hour, min, sec));
	}
}
