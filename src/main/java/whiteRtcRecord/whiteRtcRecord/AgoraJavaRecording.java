package whiteRtcRecord.whiteRtcRecord;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import whiteRtcRecord.whiteRtcRecord.Common.*;

/**
 * Created by az on 2018/5/13.
 */
public class AgoraJavaRecording {
    private boolean stopped = false;
    private boolean isMixMode = false;
    private int width = 0;
    private int height = 0;
    private int fps = 0;
    private int kbps = 0;
    private String storageDir = "./";
    private long aCount = 0;
    private long count = 0;
    private long size = 0;
    private CHANNEL_PROFILE_TYPE profile_type;
    Vector<Long> m_peers = new Vector<Long>();
    private long mNativeHandle = 0;
    private boolean IsMixMode(){
        return isMixMode;
    }

//    public enum VIDEO_FORMAT_TYPE {
//        VIDEO_FORMAT_DEFAULT_TYPE(0),
//        VIDEO_FORMAT_H264_FRAME_TYPE(1),
//        VIDEO_FORMAT_YUV_FRAME_TYPE(2),
//        VIDEO_FORMAT_JPG_FRAME_TYPE(3),
//        VIDEO_FORMAT_JPG_FILE_TYPE(4),
//        VIDEO_FORMAT_JPG_VIDEO_FILE_TYPE(5);
//        private int value;
//        private VIDEO_FORMAT_TYPE(int value) {
//            this.value = value;
//        }
//        private int getValue(){
//            return value;
//        }
//    }
//    public enum AUDIO_FORMAT_TYPE {
//        AUDIO_FORMAT_DEFAULT_TYPE(0),
//        AUDIO_FORMAT_AAC_FRAME_TYPE(1),
//        AUDIO_FORMAT_PCM_FRAME_TYPE(2),
//        AUDIO_FORMAT_MIXED_PCM_FRAME_TYPE(3);
//        private int value;
//        private AUDIO_FORMAT_TYPE(int value) {
//            this.value = value;
//        }
//        private int getValue(){
//            return value;
//        }
//    }
//    public enum CHANNEL_PROFILE_TYPE {
//        CHANNEL_PROFILE_COMMUNICATION(0),
//        CHANNEL_PROFILE_LIVE_BROADCASTING(1);
//        private int value;
//        private CHANNEL_PROFILE_TYPE(int value) {
//            this.value = value;
//        }
//        public int getValue() {
//            return value;
//        }
//    }
//    public enum REMOTE_VIDEO_STREAM_TYPE {
//        REMOTE_VIDEO_STREAM_HIGH(0),
//        REMOTE_VIDEO_STREAM_LOW(1);
//        private int value;
//        private REMOTE_VIDEO_STREAM_TYPE(int value) {
//            this.value = value;
//        }
//        public int getValue() {
//            return value;
//        }
//    }
//    class RecordingConfig {
//        public boolean isAudioOnly;
//        public boolean isVideoOnly;
//        public boolean isMixingEnabled;
//        public boolean mixedVideoAudio;
//        public String mixResolution;
//        public String decryptionMode;
//        public String secret;
//        public String appliteDir;
//        public String recordFileRootDir;
//        public String cfgFilePath;
//        //decodeVideo: default 0 (0:save as file, 1:h.264, 2:yuv, 3:jpg buffer, 4:jpg file, 5:jpg file and video file)
//        public VIDEO_FORMAT_TYPE decodeVideo;
//        //decodeAudio:  (default 0 (0:save as file, 1:aac frame, 2:pcm frame, 3:mixed pcm frame) (Can't combine with isMixingEnabled) /option)
//        public AUDIO_FORMAT_TYPE decodeAudio;
//        public int lowUdpPort;
//        public int highUdpPort;
//        public int idleLimitSec;
//        public int captureInterval;
//        //channelProfile:0 braodacast, 1:communicate; default is 1
//        public CHANNEL_PROFILE_TYPE channelProfile;
//        //streamType:0:get low stream 1:get high stream; default is 0
//        public REMOTE_VIDEO_STREAM_TYPE streamType;
//        public int triggerMode;
//    }
    static{
        try {
            System.loadLibrary("recording");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static native boolean createChannel(String appId, String channelKey, String name,  Integer uid, RecordingConfig config);

//    public static void main(String args[]){
//        int channelProfile = 0;
//
//        String decryptionMode = "";
//        String secret = "";
//        String mixResolution = "360,640,15,500";
//
//        int idleLimitSec=5*60;//300s
//
//        String recordFileRootDir = "";
//        String cfgFilePath = "";
//
//        int lowUdpPort = 0;//40000;
//        int highUdpPort = 0;//40004;
//
//        boolean isAudioOnly=false;
//        boolean isVideoOnly=false;
//        boolean isMixingEnabled=false;
//        boolean mixedVideoAudio=false;
//
//        int streamType = REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_HIGH.ordinal();
//        int captureInterval = 5;
//        int triggerMode = 0;
//
//        System.out.println(System.getProperty("java.library.path"));
//
//        AgoraJavaRecording load = new AgoraJavaRecording();
//        RecordingConfig config =  load.new RecordingConfig();
//        config.channelProfile = CHANNEL_PROFILE_TYPE.values()[channelProfile];
//        config.idleLimitSec = idleLimitSec;
//        config.isVideoOnly = isVideoOnly;
//        config.isAudioOnly = isAudioOnly;
//        config.isMixingEnabled = isMixingEnabled;
//        config.mixResolution = mixResolution;
//        config.mixedVideoAudio = mixedVideoAudio;
//        config.appliteDir = "/usr/local/Agora_Recording_SDK_for_Linux_FULL/bin";
//        config.recordFileRootDir = recordFileRootDir;
//        config.cfgFilePath = cfgFilePath;
//        config.secret = secret;
//        config.decryptionMode = decryptionMode;
//        config.lowUdpPort = lowUdpPort;
//        config.highUdpPort = highUdpPort;
//        config.captureInterval = captureInterval;
//        config.decodeAudio = AUDIO_FORMAT_TYPE.AUDIO_FORMAT_DEFAULT_TYPE;
//        config.decodeVideo = VIDEO_FORMAT_TYPE.VIDEO_FORMAT_DEFAULT_TYPE;
//        config.streamType = REMOTE_VIDEO_STREAM_TYPE.values()[streamType];
//        config.triggerMode = triggerMode;
//
//        load.isMixMode = isMixingEnabled;
//        load.profile_type = CHANNEL_PROFILE_TYPE.values()[channelProfile];
//        if(isMixingEnabled && !isAudioOnly) {
//            String[] sourceStrArray=mixResolution.split(",");
//            if(sourceStrArray.length != 4) {
//                System.out.println("Illegal resolution:"+mixResolution);
//                return;
//            }
//            load.width = Integer.valueOf(sourceStrArray[0]).intValue();
//            load.height = Integer.valueOf(sourceStrArray[1]).intValue();
//            load.fps = Integer.valueOf(sourceStrArray[2]).intValue();
//            load.kbps = Integer.valueOf(sourceStrArray[3]).intValue();
//        }
//        //run jni event loop , or start a new thread to do it
//        try {
//            load.createChannel("fd33b96dc23c417fac91579efff42701", "12345", "12345", 0, config);
//        } catch (Exception e) {
//            System.loadLibrary("create channel ");
//            e.printStackTrace();
//        }
//    }

    public static void startRecord()
    {
        int uid = 0;
        String appId = "";
        String channelKey = "";
        String name = "";
        int channelProfile = 0;

        String decryptionMode = "";
        String secret = "";
        String mixResolution = "360,640,15,500";

        int idleLimitSec=5*60;//300s

        String applitePath = "";
        String recordFileRootDir = "";
        String cfgFilePath = "";

        int lowUdpPort = 0;//40000;
        int highUdpPort = 0;//40004;

        boolean isAudioOnly=false;
        boolean isVideoOnly=false;
        boolean isMixingEnabled=false;
        boolean mixedVideoAudio=false;

        int getAudioFrame = AUDIO_FORMAT_TYPE.AUDIO_FORMAT_DEFAULT_TYPE.ordinal();
        int getVideoFrame = VIDEO_FORMAT_TYPE.VIDEO_FORMAT_DEFAULT_TYPE.ordinal();
        int streamType = REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_HIGH.ordinal();
        int captureInterval = 5;
        int triggerMode = 0;

        //paser command line parameters
        uid = Integer.parseInt("0");
        appId = String.valueOf("fd33b96dc23c417fac91579efff42701");
        name = String.valueOf("12345");
        applitePath = String.valueOf("/usr/local/Agora_Recording_SDK_for_Linux_FULL/bin");

        AgoraJavaRecording ars = new AgoraJavaRecording();
        RecordingConfig config= new RecordingConfig();
        config.channelProfile = CHANNEL_PROFILE_TYPE.values()[channelProfile];
        config.idleLimitSec = idleLimitSec;
        config.isVideoOnly = isVideoOnly;
        config.isAudioOnly = isAudioOnly;
        config.isMixingEnabled = isMixingEnabled;
        config.mixResolution = mixResolution;
        config.mixedVideoAudio = mixedVideoAudio;
        config.appliteDir = applitePath;
        config.recordFileRootDir = recordFileRootDir;
        config.cfgFilePath = cfgFilePath;
        config.secret = secret;
        config.decryptionMode = decryptionMode;
        config.lowUdpPort = lowUdpPort;
        config.highUdpPort = highUdpPort;
        config.captureInterval = captureInterval;
        config.decodeAudio = AUDIO_FORMAT_TYPE.values()[getAudioFrame];
        config.decodeVideo = VIDEO_FORMAT_TYPE.values()[getVideoFrame];
        config.streamType = REMOTE_VIDEO_STREAM_TYPE.values()[streamType];
        config.triggerMode = triggerMode;
    /*
     * change log_config Facility per your specific purpose like agora::base::LOCAL5_LOG_FCLT
     * Default:USER_LOG_FCLT.
     *
     * ars.setFacility(LOCAL5_LOG_FCLT);
     */

        System.out.println(System.getProperty("java.library.path"));

        ars.isMixMode = isMixingEnabled;
        ars.profile_type = CHANNEL_PROFILE_TYPE.values()[channelProfile];
        if(isMixingEnabled && !isAudioOnly) {
            String[] sourceStrArray=mixResolution.split(",");
            if(sourceStrArray.length != 4) {
                System.out.println("Illegal resolution:"+mixResolution);
                return;
            }
            ars.width = Integer.valueOf(sourceStrArray[0]).intValue();
            ars.height = Integer.valueOf(sourceStrArray[1]).intValue();
            ars.fps = Integer.valueOf(sourceStrArray[2]).intValue();
            ars.kbps = Integer.valueOf(sourceStrArray[3]).intValue();
        }
        //run jni event loop , or start a new thread to do it
        ars.createChannel(appId, channelKey,name,uid,config);
        System.out.println("jni layer has been exited...");
        System.exit(0);
    }
}
