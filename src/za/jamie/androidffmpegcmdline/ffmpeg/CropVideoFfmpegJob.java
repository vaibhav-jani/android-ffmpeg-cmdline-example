package za.jamie.androidffmpegcmdline.ffmpeg;
import java.util.LinkedList;
import java.util.List;


public class CropVideoFfmpegJob {

	public String inputPath;
	
	//public long startTime = -1;
	//public long duration = -1;
	
	public String outputPath;
	//public String format;
	
	private final String mFfmpegPath;
	
	public CropVideoFfmpegJob(String ffmpegPath) {
		mFfmpegPath = ffmpegPath;
	}
	
	///ffmpeg -i clipcanvas_14348_offline.mp4 -ss 0 -t 3 -vcodec copy -acodec copy test2.mp4

	public ProcessRunnable create() {
		if (inputPath == null || outputPath == null) {
			throw new IllegalStateException("Need an input and output filepath!");
		}	
		
		final List<String> cmd = new LinkedList<String>();
		
		cmd.add(mFfmpegPath);
		
		cmd.add("-i");
		
		cmd.add(inputPath);
		
		cmd.add("-ss");
		
		cmd.add("0");
		
		cmd.add("-t");
		
		cmd.add("3");
		
		cmd.add("-vcodec");
		
		cmd.add("copy");
		
		cmd.add("-acodec");
		
		cmd.add("copy");
		
		cmd.add(outputPath);
		
		final ProcessBuilder pb = new ProcessBuilder(cmd);
		return new ProcessRunnable(pb);
	}
	
	
}
