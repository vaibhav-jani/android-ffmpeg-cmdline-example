package ffmpeg;
import java.util.LinkedList;
import java.util.List;


public class RotateVideoFfmpegJob {

	public String inputPath;
	
	public String outputPath;
	//public String format;
	
	private final String mFfmpegPath;
	
	public RotateVideoFfmpegJob(String ffmpegPath) {
		mFfmpegPath = ffmpegPath;
	}
	
	///ffmpeg -i in.mov -vf "transpose=1" out.mov

	public ProcessRunnable create() {
		if (inputPath == null || outputPath == null) {
			throw new IllegalStateException("Need an input and output filepath!");
		}	
		
		final List<String> cmd = new LinkedList<String>();
		
		cmd.add(mFfmpegPath);
		
		cmd.add("-i");
		
		cmd.add(inputPath);
		
		cmd.add("-vf");
		
		cmd.add("transpose=1");
		
		/*cmd.add("-vcodec");
		
		cmd.add("copy");
		
		cmd.add("-acodec");
		
		cmd.add("copy");*/
		
		cmd.add("-strict");
		
		cmd.add("-2");
		
		cmd.add(outputPath);
		
		final ProcessBuilder pb = new ProcessBuilder(cmd);
	
		return new ProcessRunnable(pb);
	}
	
	
	
	
}
