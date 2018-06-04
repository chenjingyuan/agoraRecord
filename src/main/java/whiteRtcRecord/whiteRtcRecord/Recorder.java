package whiteRtcRecord.whiteRtcRecord;

import io.agora.recording.RecordingSDK;

public class Recorder {
    private static RecordingSDK recordingSDKinstance = new RecordingSDK();
    private static RecordingService recordingService = new RecordingService(recordingSDKinstance);
    private Recorder (){}
    public static RecordingService getInstance() {
        return recordingService;
    }

}
