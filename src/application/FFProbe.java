package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import javafx.application.Platform;

public class FFProbe {
	private String ffprobeEXE;
	private String timeString, width, height;
	private int totalTime;
	private String pos;
	private String audio;
	private String video;
	private String m3u8Audio;
	private String m3u8Video;
	private double m3u8Time;
	private String m3u8TimeString;
	private String m3u8Res;
	private boolean sucess = true;
	private boolean validLink = false;
	
	public FFProbe(String ffprobeEXE) {
		super();
		this.ffprobeEXE = ffprobeEXE;
	}
	
	public void getVideoInfo(String videoInputPath) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		command.add("-show_entries");
		command.add("format=duration:stream=width,height");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		
		CountDownLatch latch = new CountDownLatch(1);
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		
		new Thread() {
			public void run() {
				Scanner sc = new Scanner(process.getInputStream());
				try {
					Pattern widthPattern = Pattern.compile("(?<=width=)[\\d]*");
			        String match = sc.findWithinHorizon(widthPattern, 0);
			        width = match;
			        
			        Pattern heightPattern = Pattern.compile("(?<=height=)[\\d]*");
			        match = sc.findWithinHorizon(heightPattern, 0);
			        height = match;
			        
			        Pattern durPattern = Pattern.compile("(?<=duration=)[\\d+.]*");
			        match = sc.findWithinHorizon(durPattern, 0);
			        String[] time = match.split("\\:|\\.");
			        int hour = Integer.parseInt(time[0])/3600;
			        int min = (Integer.parseInt(time[0])% 3600)/60;
			        int sec = Integer.parseInt(time[0])%60;
			        int mili = Integer.parseInt(time[1]) / 10000;
			        timeString = String.format("%02d:%02d:%02d.%02d", hour, min, sec, mili);
			        totalTime = Integer.parseInt(time[0]);
				}
				catch (Exception e) {
					sucess = false;
				}
		        sc.close();
		        latch.countDown();
			}
		}.start();
		
		latch.await();
	}
	
	public void getVideoTime(String videoInputPath) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		command.add("-show_entries");
		command.add("format=duration:stream=width,height");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		CountDownLatch latch = new CountDownLatch(1);
		
		new Thread() {
			public void run() {
				Scanner sc = new Scanner(process.getInputStream());
		        Pattern durPattern = Pattern.compile("(?<=duration=)[\\d+.]*");
		        String match = sc.findWithinHorizon(durPattern, 0);
		        
		        String[] time = match.split("\\:|\\.");
		        int hour = Integer.parseInt(time[0])/3600;
		        int min = (Integer.parseInt(time[0])% 3600)/60;
		        int sec = Integer.parseInt(time[0])%60;
		        int mili = Integer.parseInt(time[1]) / 10000;
		        timeString = String.format("%02d:%02d:%02d.%02d", hour, min, sec, mili);
		        Platform.runLater(new Runnable() {
					@Override
					public void run() {
						totalTime = Integer.parseInt(time[0]);
					}
		        });
		        sc.close();
		        latch.countDown();
			}
		}.start();
		latch.await();
	}
	
	public void getVideoTimeM3U8(String videoInputPath) throws IOException, InterruptedException{
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		command.add("-protocol_whitelist");
    	command.add("file,http,https,tcp,tls,crypto");
		command.add("-show_entries");
		command.add("format=duration:stream=codec_name,codec_type,width,height");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		CountDownLatch latch = new CountDownLatch(1);
		
		new Thread() {
			public void run() {
				try {
					Scanner sc = new Scanner(process.getInputStream());
					Pattern codecPattern = Pattern.compile("(?<=codec_name=)[\\w+.]*");
					boolean audioFound = false;
					boolean videoFound = false;
					boolean found = false;
					
					while(!found) {
						String temp = sc.findWithinHorizon(codecPattern, 0);
						Pattern codecType = Pattern.compile("(?<=codec_type=)[\\w+.]*");
						String type = sc.findWithinHorizon(codecType, 0);
						if(type.equals("video")) {
							m3u8Video = temp;
							videoFound = true;
							if(audioFound) {
								found = true;
							}
						}
						else if(type.equals("audio")) {
							m3u8Audio = temp;
							audioFound = true;
							if(videoFound) {
								found = true;
							}
						}
					}
					Pattern widthPattern = Pattern.compile("(?<=width=)[\\d+.]*");
			        String width = sc.findWithinHorizon(widthPattern, 0);
			        
			        Pattern heightPattern = Pattern.compile("(?<=height=)[\\d+.]*");
			        String height = sc.findWithinHorizon(heightPattern, 0);
			        m3u8Res = width + " x " + height; 
			        
					Pattern durPattern = Pattern.compile("(?<=duration=)[\\d+.]*");
			        String match = sc.findWithinHorizon(durPattern, 0);
			        String[] time = match.split("\\.");
			        int hour = Integer.parseInt(time[0])/3600;
			        int min = (Integer.parseInt(time[0])% 3600)/60;
			        int sec = Integer.parseInt(time[0])%60;
			        int mili = Integer.parseInt(time[1]) / 10000;
			        m3u8TimeString = String.format("%02d:%02d:%02d.%02d", hour, min, sec, mili);
			        
					m3u8Time = Double.parseDouble(match);
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							totalTime = Integer.parseInt(time[0]);
						}
			        });
			        sc.close();
			        latch.countDown();
				}
				catch (Exception e) {
					
				}
			}
		}.start();
		latch.await();
	}
	
	public void getVideoPOS(String videoInputPath) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		if(FilenameUtils.getExtension(videoInputPath).equals("m3u8")) {
			command.add("-protocol_whitelist");
        	command.add("file,http,https,tcp,tls,crypto");
		}
		command.add("-show_entries");
		command.add("packet=pos");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		CountDownLatch latch = new CountDownLatch(1);
		System.out.println(command);
		
		new Thread() {
			public void run() {
				Scanner sc = new Scanner(process.getInputStream());
		        
		        while(sc.hasNextLine()) {
		        	String temp = sc.nextLine();
		        	
		        	if(!temp.substring(4).equals("N/A")) {
		        		pos = temp.substring(4);
		        	}
//		        	System.out.println(sc.nextLine());
		        }
		        sc.close();
		        latch.countDown();
			}
		}.start();
		latch.await();
	}
	
	public void getCodecs(String videoInputPath) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		command.add("-show_entries");
		command.add("stream=codec_name,codec_type");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		CountDownLatch latch = new CountDownLatch(1);
		
		new Thread() {
			public void run() {
				Scanner sc = new Scanner(process.getInputStream());
				Pattern codecPattern = Pattern.compile("(?<=codec_name=)[\\w+.]*");
				boolean audioFound = false;
				boolean videoFound = false;
				boolean found = false;
				
				while(!found) {
					String temp = sc.findWithinHorizon(codecPattern, 0);
					Pattern codecType = Pattern.compile("(?<=codec_type=)[\\w+.]*");
					String type = sc.findWithinHorizon(codecType, 0);
					if(type.equals("video")) {
						video = temp;
						videoFound = true;
						if(audioFound || (!sc.hasNext())) {
							found = true;
						}
					}
					else if(type.equals("audio")) {
						audio = temp;
						audioFound = true;
						if(videoFound || (!sc.hasNext())) {
							found = true;
						}
					}
				}
		        sc.close();
		        latch.countDown();
			}
		}.start();
		latch.await();
	}
	
	public void validateURL(String videoInputPath) throws IOException, InterruptedException{
		List<String> command = new ArrayList<>();
		command.add(ffprobeEXE);
		command.add(videoInputPath);
		command.add("-protocol_whitelist");
    	command.add("file,http,https,tcp,tls,crypto");
		command.add("-show_entries");
		command.add("format=duration:stream=codec_name,codec_type,width,height");
		command.add("-of");
		command.add("default=noprint_wrappers=1");
		command.add("-v");
		command.add("error");
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();	
		CountDownLatch latch = new CountDownLatch(1);
		
		new Thread() {
			public void run() {
				try {
					Scanner sc = new Scanner(process.getInputStream());
					Pattern err = Pattern.compile("(403)|Invalid");
					validLink = false;
					
					while(sc.hasNext() && sc.nextLine() != null) {
						String match = sc.findWithinHorizon(err, 0);
						if(match == null) {
							validLink = true;
							break;
						}
					}
			        sc.close();
			        latch.countDown();
				}
				catch (Exception e) {
					
				}
			}
		}.start();
		latch.await();
	}
	
	public String getWidth() {
		return width;
	}
	
	public String getHeight() {
		return height;
	}
	
	public String getTime() {
		return timeString;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	public double getM3U8Time() {
		return m3u8Time;
	}
	
	public String getM3U8TimeString() {
		return m3u8TimeString;
	}
	
	public String getM3U8Res() {
		return m3u8Res;
	}
	
	public String getM3U8Audio(){
		return m3u8Audio.toUpperCase();
	}
	
	public String getM3U8Video(){
		return m3u8Video.toUpperCase();
	}
	
	public double getPOS() {
		return Double.parseDouble(pos);
	}
	
	public boolean isValidLink() {
		return validLink;
	}
	
	public boolean isSucess() {
		return sucess;
	}
	
	public String getVideoCodec() {
		if(video == null) {
			return "No Video";
		}else {
			return video.toUpperCase();
		}
	}
	
	public String getAudioCodec() {
		if(audio == null) {
			return "No Audio";
		}else {
			return audio.toUpperCase();
		}
	}
}
