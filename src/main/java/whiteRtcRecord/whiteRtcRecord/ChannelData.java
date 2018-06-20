package whiteRtcRecord.whiteRtcRecord;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by az on 2018/6/17.
 */
public class ChannelData {
    // 用于记录房间的录制状态
    private static Map<String, Boolean> channelsRecordingStates = new ConcurrentHashMap<>();
    // 记录所有房间的用户id和录制文件信息
    private static Map<String, Map<Long, RecodingFile>> recordingUserAndFiles = new ConcurrentHashMap<>();

    private ChannelData() {}

    public static Map<String, Boolean>  getChannelsRecordingStates() {
        return channelsRecordingStates;
    }

    public static Map<String, Map<Long, RecodingFile>> getRecordingUserAndFiles() {
        return recordingUserAndFiles;
    }
}
