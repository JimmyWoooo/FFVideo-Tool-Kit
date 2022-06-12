package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import components.fileOutput;

public class FFMPEG {
	private String ffmpegEXE;
	String tempDir = System.getProperty("java.io.tmpdir");
	File pic = new File(tempDir + "output.png");
	boolean done = false;
	String timeString;
	File file;
	boolean sucess;
	double totalSecs;
	boolean startCheck = false;
	boolean endCheck = false;
	boolean fileChecked = false;
	FFProbe ffprobe = new FFProbe("ffprobe.exe");
	double pos;
	AlertBox alert = new AlertBox();
	boolean cantCopyVideo = false;
	boolean cantCopyAudio = false;
	
	public FFMPEG(){
		this.ffmpegEXE = "ffmpeg.exe";
	}
	
	public void getThumbnail(String videoInputPath) throws Exception {
		file = new File(videoInputPath);
		List<String> command = new ArrayList<>();
		String temp = tempDir.toString() + "output.png";
		command.add(ffmpegEXE);  
		command.add("-ss");
        command.add("00:00:01.000");
        command.add("-i");
		command.add(videoInputPath);
		command.add("-vframes");
		command.add("1");
		command.add(temp);
		command.add("-hide_banner");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.start();	
	}
	
	public void initialize(File files, fileOutput out) throws IOException {
		String path = out.getDest();
		String output = path + "\\" + out.getOutputFileName() + out.getExtension();
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		if(out.getId() == "trim") {
			trimVideo(files, out, command);
		}
		else if(out.getId() == "merge") {
			mergeVideos(out.merge.getList(), out, command);
		}
		else if(out.getId() == "crop") {
			cropVideo(files, out, command);
		}
		else if(out.getId() == "overlay") {
			overlayVideo(files, out, command);
		}
		else if(out.getId() == "decimate") {
			decimateVideo(files, out, command);
		}
		else if(out.getId() == "m3u8") {
			m3u8Video(files, out, command);
		}

		command.add(output);
		command.add("-hide_banner");
		
		if(sucess) {
			checkFiles(out, output, files);
		}
		
		if(fileChecked) {
			System.out.println("started");
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			
			new Thread() {
				public void run() {
					out.start.setDisable(true);
					Scanner sc = new Scanner(process.getErrorStream());
					String endTime = "--:--:--";
					if(out.getId() != "merge" && out.getId() != "decimate" && out.getId() != "m3u8") {
						endTime = out.getTimeStamps().getTimeDiff(out.getVidDetails().getTotalTime());
					}
					if(out.getId() == "decimate") {
						out.getOutputInfo().setStatus("Seeking End Time");
						try {
							if(!out.getDec().m3u8Check()) {
								ffprobe.getVideoPOS(files.getAbsolutePath());
							}
							else if(out.getDec().getM3U8().localRadio()) {
								ffprobe.getVideoPOS(out.getDec().getM3U8().getLocalPath());
							}
							else if(out.getDec().getM3U8().urlRadio()) {
								ffprobe.getVideoPOS(out.getDec().getM3U8().getUrlPath());
							}
						}
						catch(IOException | InterruptedException e) {
							alert.display(e.toString());
						}
						out.getOutputInfo().setStatus("Running");
						out.getOutputInfo().setEnd(endTime);
						displayInfoDecimate(sc, out, process);
					}
					else if(out.getId() == "m3u8") {
						if(out.getM3U8().getM3U8().localRadio()) {
							try {
								ffprobe.getVideoTimeM3U8(out.getM3U8().getM3U8().getLocalPath());
								endTime = out.getTimeStamps().getTimeDiff(ffprobe.getTotalTime());
								if(!startCheck && !endCheck) {
									totalSecs = ffprobe.getM3U8Time();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						out.getOutputInfo().setStatus("Downloading..");
						out.getOutputInfo().setEnd(endTime);
						displayInfo(sc, out, process);
					}
					else {
						if(out.getId() == "trim" || out.getId() == "overlay" || out.getId() == "crop") {
							if(!startCheck && !endCheck) {
								totalSecs = out.getVidDetails().getTotalTime();
							}
						}
						if(out.getId() == "merge") {
							for(int x = 0; x < out.merge.getList().size(); x++) {
								try {
									ffprobe.getVideoTime(out.merge.getList().get(x));
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							totalSecs += ffprobe.getTotalTime();
						}
						out.getOutputInfo().setStatus("Running");
						out.getOutputInfo().setEnd(endTime);
						displayInfo(sc, out, process);
					}
				}
			}.start();
		}
		System.out.println(command);
	}
	
	public void trimVideo(File files, fileOutput out, List<String> command) throws IOException {
		if(out.checkTime()) {
			setTime(out, command);
	        command.add("-i");
			command.add(files.getAbsolutePath());
			addCodecs(out, command);
			volumeFilter(out, command);
			setScale(out, command);
			if((cantCopyVideo && out.getCopy()) || (cantCopyVideo && out.getVCB() == "Copy")) {
				alert.display("Video Filters were applied, can't copy.");
			}
			else if((cantCopyAudio && out.getCopy()) || (cantCopyAudio && out.getACB() == "Copy")){
				alert.display("Audio Filters were applied, can't copy.");
			}
			else if((cantCopyAudio || cantCopyVideo) && out.getCopy()){
				alert.display("Filters were applied, can't copy.");
			}
			else {
				sucess = true;
			}
		}
		else {
			alert.display("Invalid time.");
		}
	}
		
	public void mergeVideos(ArrayList<String> list, fileOutput out, List<String> command) throws IOException {
		if(list.size() < 2) {
			alert.display("Not enough videos.");
		}else {
			Path p = Paths.get(tempDir + "files.txt");
			if(Files.exists(p)) {
				Files.delete(p);
			}
			File f = new File(tempDir + "files.txt");
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);
			for(int x = 0; x < list.size(); x++) {
				pw.write("file '" + list.get(x) + "'\n");
			}
			pw.close();
			command.add("-f");
			command.add("concat");
			command.add("-safe");
			command.add("0");
			command.add("-i");
			command.add(f.getAbsolutePath());
			command.add("-c");
			command.add("copy");
			sucess = true;
		}
	}
	
	public void cropVideo(File files, fileOutput out, List<String> command) {
		int x = out.getCrop().getX();
		int y = out.getCrop().getY();
		int w = out.getCrop().getW();
		int h = out.getCrop().getH();
		cantCopyVideo = true;
		if(out.checkTime()) {
			setTime(out, command);
		}
		else {
			alert.display("Invalid time.");
		}
		if(out.getCrop().validate()) {
			command.add("-i");
			command.add(files.getAbsolutePath());
			command.add("-filter:v");
			command.add("\"crop=" + w + ":" + h + ":" + x + ":" + y + "\"");
			addCodecs(out, command);
			volumeFilter(out, command);
			if(cantCopyVideo && out.getVCB() == "Copy") {
				alert.display("Video Filters were applied, can't copy.");
			}
			else if(cantCopyAudio && out.getACB() == "Copy"){
				alert.display("Audio Filters were applied, can't copy.");
			}
			else {
				sucess = true;
			}
		}else {
			alert.display("Invalid input.");
		}
	}
	
	public void overlayVideo(File files, fileOutput out, List<String> command) {
		cantCopyVideo = true;
		if(!out.getOverlay().pathExists()) {
			alert.display("No image/gif.");
		}
		if(!out.getOverlay().validInput()) {
			alert.display("Missing inputs.");
		}
		if(out.getOverlay().validInput() && out.getOverlay().pathExists()) {
			int degrees = 0;
			if(out.getOverlay().isRotated()) {
				degrees = out.getOverlay().getDegrees();
			}
			command.add("-i");
			command.add(files.getAbsolutePath());
			if(out.getOverlay().getId() == "gif") {
				command.add("-ignore_loop");
				command.add("0");
			}
			command.add("-i");
			command.add(out.getOverlay().getPath());
			command.add("-filter_complex");
			if(out.getOverlay().isRotated() && !out.getOverlay().isScaled()) {
				if(out.getOverlay().getId() == "image") {
					command.add("\"[1:v]rotate=" + degrees + "*PI/180:c=none:ow=rotw(" + degrees 
							+ "*PI/180):oh=roth(" + degrees + "*PI/180)[rotate]; [0:v][rotate]" + out.getOverlay().getRadioSelected() + "\"");
				}
				if(out.getOverlay().getId() == "gif") {
					command.add("\"[1:v]rotate=" + degrees + "*PI/180:c=none:ow=rotw(" + degrees 
							+ "*PI/180):oh=roth(" + degrees + "*PI/180)[rotate]; [0:v][rotate]" + out.getOverlay().getRadioSelected() + ":shortest=1\"");
				}
			}
			else if(!out.getOverlay().isRotated() && out.getOverlay().isScaled()){
				if(out.getOverlay().getId() == "image") {
					command.add("\"[1:v]scale=" + out.getOverlay().getValues() + "[img];[0:v][img]" + out.getOverlay().getRadioSelected() + "\"");
				}
				if(out.getOverlay().getId() == "gif") {
					command.add("\"[1:v]scale=" + out.getOverlay().getValues() +  "[gif];[0:v][gif]" + out.getOverlay().getRadioSelected() + ":shortest=1\"");
				}
			}
			else if(out.getOverlay().isRotated() && out.getOverlay().isScaled()){
				if(out.getOverlay().getId() == "image") {
					command.add("\"[1:v]scale=" + out.getOverlay().getValues() + ",rotate=" + degrees + "*PI/180:c=none:ow=rotw(" + degrees 
							+ "*PI/180):oh=roth(" + degrees + "*PI/180)[rotate]; [0:v][rotate]" + out.getOverlay().getRadioSelected() + "\"");
				}
				if(out.getOverlay().getId() == "gif") {
					command.add("\"[1:v]scale=" + out.getOverlay().getValues() + ",rotate=" + degrees + "*PI/180:c=none:ow=rotw(" + degrees 
							+ "*PI/180):oh=roth(" + degrees + "*PI/180)[rotate]; [0:v][rotate]" + out.getOverlay().getRadioSelected() + ":shortest=1\"");
				}
			}
			else {
				if(out.getOverlay().getId() == "image") {
					command.add("\""+ out.getOverlay().getRadioSelected() + "\"");
				}
				if(out.getOverlay().getId() == "gif") {
					command.add("\"[0][1]" + out.getOverlay().getRadioSelected() + ":shortest=1\"");
				}
			}
			addCodecs(out, command);
			if(cantCopyVideo && out.getVCB() == "Copy") {
				alert.display("Video Filters were applied, can't copy.");
			}
			else if(cantCopyAudio && out.getACB() == "Copy"){
				alert.display("Audio Filters were applied, can't copy.");
			}
			else {
				sucess = true;
			}
		}
	}
	
	public void decimateVideo(File files, fileOutput out, List<String> command) {
		if(out.getDec().m3u8Check() && out.checkDecimateM3U8Time()) {
			boolean validateM3U8 = false;
			if(out.getDec().getM3U8().localRadio()) {
				File file = new File(out.getDec().getM3U8().getLocalPath());
				if(out.getDec().getM3U8().getLocalPath().equals("")) {
					alert.display("No m3u8 file chosen.");
				}
				else if(!file.exists()) {
					alert.display(file.getName() + " no longer exists.");
				}
				else {
					validateM3U8 = true;
				}
			}
			if(out.getDec().getM3U8().urlRadio()) {
				if(out.getDec().getM3U8().getUrlPath().equals("")) {
					alert.display("No URL input.");
				}
				else if(!out.getDec().getM3U8().getUrlPath().equals("")) {
					if(out.getDec().getM3U8().validateURL()) {
						validateM3U8 = true;
					}else {
						alert.display("Invalid URL");
					}
				}
			}
			if(validateM3U8) {
				command.add("-protocol_whitelist");
	        	command.add("file,http,https,tcp,tls,crypto");
				setTimeM3U8(out, command);
		        command.add("-i");
		        if(out.getDec().getM3U8().urlRadio()) {
		        	command.add(out.getDec().getM3U8().getUrlPath());
		        }
		        else if(out.getDec().getM3U8().localRadio()) {
		        	command.add(out.getDec().getM3U8().getLocalPath());
		        }
	        	command.add("-vf");
				command.add("mpdecimate,setpts=N/FRAME_RATE/TB,showinfo");
				command.add("-an");
				sucess = true;
			}
		}
		else {
			if(out.getVidDetails().getFile() == null) {
				alert.display("No input file.");
			}
			else {
				if(out.checkTime()) {
					setTime(out, command);
			        command.add("-i");
					command.add(files.getAbsolutePath());
					command.add("-vf");
					command.add("mpdecimate,setpts=N/FRAME_RATE/TB,showinfo");
					command.add("-c:v");
					command.add("libx264");
					command.add("-an");
					sucess = true;
				}
				else {
					alert.display("Invalid time.");
				}
			}
		}
		
	}
	
	public void m3u8Video(File files, fileOutput out, List<String> command) {
		boolean validateM3U8 = false;
		boolean validateTV = true;
		if(out.getM3U8().getM3U8().localRadio()) {
			File file = new File(out.getM3U8().getM3U8().getLocalPath());
			if(out.getM3U8().getM3U8().getLocalPath().equals("")) {
				alert.display("No m3u8 file chosen.");
			}
			else if(!file.exists()) {
				alert.display(file.getName() + " no longer exists.");
			}
			else {
				validateM3U8 = true;
			}
		}
		if(out.getM3U8().getM3U8().urlRadio()) {
			if(out.getM3U8().getM3U8().getUrlPath().equals("")) {
				alert.display("No URL input.");
			}
			else if(!out.getM3U8().getM3U8().getUrlPath().equals("")) {
				if(out.getM3U8().getM3U8().validateURL()) {
					validateM3U8 = true;
				}else {
					alert.display("Invalid URL");
				}
			}
		}
		if(validateM3U8) {
			if((out.getM3U8().getRadioSelected() == "copy" && out.getVol()/100 != 1.0)){
				alert.display("Can't copy and apply volume filter.");
				validateTV = false;
			}
			else if(!out.checkM3U8Time()) {
				alert.display("Invalid time.");
				validateTV = false;
			}
			if(validateTV) {
				command.add("-protocol_whitelist");
	        	command.add("file,http,https,tcp,tls,crypto");
				setTimeM3U8(out, command);
		        command.add("-i");
		        if(out.getM3U8().getM3U8().urlRadio()) {
		        	command.add(out.getM3U8().getM3U8().getUrlPath());
		        }
		        else if(out.getM3U8().getM3U8().localRadio()) {
		        	command.add(out.getM3U8().getM3U8().getLocalPath());
		        }
		        if(out.getM3U8().getRadioSelected() == "copy") {
		        	command.add("-c");
		        	command.add("copy");
		        }
				volumeFilter(out, command);
	        	command.add("-bsf:a");
	        	command.add("aac_adtstoasc");
				sucess = true;
			}
		}
	}
	
	public void volumeFilter(fileOutput out, List<String> command) {
		double vol = out.getVol()/100;
		if(vol != 1.0) {
			cantCopyAudio = true;
			command.add("-filter:a");
			command.add("volume="+vol);
		}
	}
	
	public void setTime(fileOutput out, List<String> command) {
		String start = out.getStart();
		String end = out.getEnd();
		
		if(start != "00:00:00" && start != null) {
			command.add("-ss");
			command.add(start);
			totalSecs = out.getVidDetails().getTotalTime() - out.getTotalStart();
			startCheck = true;
		}
		if(end != "00:00:00" && end != null) {
			command.add("-to");
			command.add(end);
			totalSecs = out.getTotalEnd();
			endCheck = true;
		}
		if(startCheck && endCheck) {
			totalSecs = out.getTotalEnd() - out.getTotalStart();
		}
	}
	
	public void setTimeM3U8(fileOutput out, List<String> command) {
		String start = out.getStart();
		String end = out.getEnd();
		
		if(start != "00:00:00" && start != null) {
			command.add("-ss");
			command.add(start);
			totalSecs = out.getM3U8().getM3U8().getM3U8Time() - out.getTotalStart();
			startCheck = true;
		}
		if(end != "00:00:00" && end != null) {
			command.add("-to");
			command.add(end);
			totalSecs = out.getTotalEnd();
			endCheck = true;
		}
		if(startCheck && endCheck) {
			totalSecs = out.getTotalEnd() - out.getTotalStart();
		}
	}
	
	public void setScale(fileOutput out, List<String> command) {
		String scale = out.getScale().replace(" x ", ":");
		
		if(scale != "Same") {
			cantCopyVideo = true;
			command.add("-vf");
			command.add("scale="+scale);
		}
	}
	
	public void displayInfo(Scanner sc, fileOutput out, Process process) {
		out.pb.setProcess(process);
		out.getOutputInfo().startTimer();
		Pattern framePattern = Pattern.compile("(?<=frame=)[\\s]*\\d*");
		String frameMatch;
		while (null != (frameMatch = sc.findWithinHorizon(framePattern, 0))) {
			Pattern sizePattern = Pattern.compile("(?<=size=)[\\s]*\\d*");
			String sizeMatch = sc.findWithinHorizon(sizePattern, 0);
			
			Pattern timePattern = Pattern.compile("(?<=time=)[^ ]*");
			String[] matchSplit;
			String timeMatch = sc.findWithinHorizon(timePattern, 0);
			matchSplit = timeMatch.split(":");
			int hour = Integer.parseInt(matchSplit[0]) * 3600;
            int minute = Integer.parseInt(matchSplit[1]) * 60;
            double second = Double.parseDouble(matchSplit[2]);
            double progress = (hour + minute + second) / totalSecs;
            
            Pattern speedPattern = Pattern.compile("(?<=speed=)[\\s]*[\\d.]*");
			String speedMatch = sc.findWithinHorizon(speedPattern, 0);
            
            out.pb.setProgress(progress);
            out.getOutputInfo().setInfo(timeMatch.split("\\.")[0], frameMatch.trim(), speedMatch.trim(), sizeMatch, progress);
        }
		finishPB(out);
		sc.close();
	}
	
	public void displayInfoDecimate(Scanner sc, fileOutput out, Process process){
		out.getOutputInfo().startTimer();
		out.pb.setProcess(process);
		
		while (sc.hasNext()) {
			Pattern posPattern = Pattern.compile("(?<=pos:)[\\s]*[\\d.]*");
			String posMatch = sc.findWithinHorizon(posPattern, 0);
			
			Pattern framePattern = Pattern.compile("(?<=frame=)[\\s]*\\d*");
			String frameMatch = sc.findWithinHorizon(framePattern, 0);
			
			Pattern sizePattern = Pattern.compile("(?<=size=)[\\s]*\\d*");
			String sizeMatch = sc.findWithinHorizon(sizePattern, 0);
			
			Pattern timePattern = Pattern.compile("(?<=time=)[^ ]*");
			String timeMatch = sc.findWithinHorizon(timePattern, 0);
			
			Pattern speedPattern = Pattern.compile("(?<=speed=)[\\s]*[\\d.]*");
			String speedMatch = sc.findWithinHorizon(speedPattern, 0);
			
            if((posMatch != null) && (sizeMatch != null) && (frameMatch != null) && (timeMatch != null) &&(speedMatch != null)) {
    			String[] matchSplit = timeMatch.split(":");
    			double progress = Double.parseDouble(posMatch)/ffprobe.getPOS();
                out.pb.setProgress(progress);
            	out.getOutputInfo().setInfo(timeMatch.split("\\.")[0], frameMatch.trim(), speedMatch, sizeMatch, progress);
            }
            if((frameMatch == null) && (sizeMatch == null) && (frameMatch == null) && (timeMatch == null) &&(speedMatch == null)) {
            	break;
            }
        }
		finishPB(out);
		sc.close();
	}
	
	public void checkFiles(fileOutput out, String output, File files) {
		File checkOutput = new File(output);
		Pattern pattern = Pattern.compile("[\\?%*:|\"<>]");
		Matcher matcher = pattern.matcher(checkOutput.getName());
		if(checkOutput.exists()) {
			alert.displayConfirm(checkOutput);
		}
		if(out.getId() == "decimate" && out.getDec().m3u8Check() && !checkOutput.exists()) {
			if(out.getDec().getM3U8().localRadio()) {
				if(!out.getDec().getM3U8().getLocalPath().equals("")) {
					fileChecked = true;
				}
			}
			if(out.getDec().getM3U8().urlRadio()) {
				if(!out.getDec().getM3U8().getUrlPath().equals("")) {
					fileChecked = true;
				}
			}
		}
		else if(out.getId() == "decimate" && !out.getDec().m3u8Check() && !checkOutput.exists()) {
			fileChecked = true;
		}
		else if(out.getId() != "merge" && out.getId() != "decimate" && out.getId() != "m3u8" && !files.exists()) {
			alert.displayFileNoLongerExists(files);
		}
		else if(out.getId() != "merge" && out.getId() != "decimate" && out.getId() != "m3u8" && files.exists() && !checkOutput.exists()) {
			fileChecked = true;
		}
		else if(out.getId() == "m3u8" && !checkOutput.exists()) {
			fileChecked = true;
		}
		else if(out.getId() == "merge" && !checkOutput.exists()) {
			boolean notFound = false;
			List<String> paths = out.merge.getList();
			for(int x = 0; x < paths.size(); x++) {
				if(!new File(paths.get(x)).exists()) {
					notFound = true;
				}
			}
			if(!notFound) {
				fileChecked = true;
			}
			else {
				alert.display("Missing file(s).");
			}
		}
		if(matcher.find()) {
			alert.display("File name can't contain '? % * : | \\ // \" < > ;");
			fileChecked = false;
		}
		
	}
		
	public void finishPB(fileOutput out) {
		out.getOutputInfo().stopTimer();
		if(!out.pb.cancelled) {
			out.pb.setProgress(1.0);
			out.getOutputInfo().setStatus("Completed");
		}
		else {
			out.getOutputInfo().setStatus("Cancelled");
		}
		out.start.setDisable(false);
	}
	
	public void addCodecs(fileOutput out, List<String> command) {
		if(out.getCopy()) {
			command.add("-c");
			command.add("copy");
		}else {
			out.getVideoValue(command);
			out.getAudioValue(command);
		}
	}
}