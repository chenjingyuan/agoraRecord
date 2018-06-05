package whiteRtcRecord.whiteRtcRecord;

/**
 * Created by az on 2018/5/19.
 */
public class RecordingConfigForm {
    public Boolean isAudioOnly = false;
    public Boolean isVideoOnly = false;
    public Boolean isMixingEnabled = false;
    public Boolean mixedVideoAudio = false;
    public String mixResolution = "360,640,15,500";
    public String decryptionMode = "";
    public String secret = "";
    public String appliteDir = "";
    public String recordFileRootDir = "";
    public String cfgFilePath = "";
    public Integer decodeVideo = 0;
    public Integer decodeAudio = 0;
    public Integer lowUdpPort = 0;
    public Integer highUdpPort = 0;
    public Integer idleLimitSec = 5 * 60;// 300s;
    public Integer captureInterval = 5;
    public Integer channelProfile = 0;
    public Integer streamType = 0;
    public Integer triggerMode = 0;
}
