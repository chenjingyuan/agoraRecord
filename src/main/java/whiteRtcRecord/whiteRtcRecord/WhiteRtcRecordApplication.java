package whiteRtcRecord.whiteRtcRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class WhiteRtcRecordApplication{

	@RequestMapping(value="/rtcFile/{channelId}", method= RequestMethod.GET)
	public String startRecord(@PathVariable String channelId){
//		AgoraJavaRecording agoraJavaRecording = new AgoraJavaRecording();
		AgoraJavaRecording.startRecord();
		return "start record";
	}

	@RequestMapping(value="/rtcFile/ended/{channelId}", method= RequestMethod.PUT)
	public String endRecord(String channelId) {
		return "end record";
	}

	public static void main(String[] args) {
		SpringApplication.run(WhiteRtcRecordApplication.class, args);
	}
}
