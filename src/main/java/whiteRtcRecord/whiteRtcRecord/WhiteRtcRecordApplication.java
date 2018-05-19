package whiteRtcRecord.whiteRtcRecord;

import io.agora.record.AgoraJavaRecording;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class WhiteRtcRecordApplication{
	@Value("${appId}")
	private String appId;
	@Value("${appliteDir}")
	private String appliteDir;
	@Value("${uid}")
	private String defaultUid;
	@Value("${libraryPath}")
	private String libraryPath;

	@RequestMapping(value="/rtcFile/{channelId}:start", method= RequestMethod.GET)
	public String startRecord(@PathVariable String channelId){
		System.out.println(System.getProperty("java.library.path"));
		String[] para = new String[] {"--appId", appId,"--uid", defaultUid,
				"--channel", channelId ,"--appliteDir",appliteDir
		};
		AgoraJavaRecording agoraJavaRecording = new AgoraJavaRecording(libraryPath,false);
		RecordingService  recordingService = new RecordingService(agoraJavaRecording);
		recordingService.startRecord(para);
		return "success";
	}

	@RequestMapping(value="/rtcFile/{channelId}:end", method= RequestMethod.PUT)
	public String endRecord(String channelId) throws Exception {
		AgoraJavaRecording agoraJavaRecording = new AgoraJavaRecording(libraryPath,false);
		RecordingService  recordingService = new RecordingService(agoraJavaRecording);
		recordingService.stopRecord(channelId);
		return "end record";
	}

	public static void main(String[] args) {
		SpringApplication.run(WhiteRtcRecordApplication.class, args);
	}
}
