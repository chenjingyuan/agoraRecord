package whiteRtcRecord.whiteRtcRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import whiteRtcRecord.whiteRtcRecord.service.RecordingService;

@RestController
@SpringBootApplication
public class WhiteRtcRecordApplication{
	@Autowired
	private RecordingService recordingService;

	@RequestMapping(value="/rtcFile/{channelId}:start", method= RequestMethod.GET)
	public String startRecord(@PathVariable String channelId,
							  @RequestParam String roomToken){
		recordingService.startRecord(channelId, roomToken);
		return "success";
	}

	@RequestMapping(value="/rtcFile/{channelId}:end", method= RequestMethod.GET)
	public String endRecord(String channelId) throws Exception {
		recordingService.stopRecord(channelId);
		return "end record";
	}

	public static void main(String[] args) {
		SpringApplication.run(WhiteRtcRecordApplication.class, args);
	}
}
