package whiteRtcRecord.whiteRtcRecord;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by az on 2018/6/17.
 */
public class ChannelData {

    @Value("${endpoint}")
    static String endpoint;
    @Value("${accessKeyId}")
    static String accessKeyId;
    @Value("${accessKeySecret}")
    static String accessKeySecret;
    @Value("${bucket}")
    static String bucket;

    // 用于记录房间的录制状态
    private static Map<String, Boolean> channelsRecordingStates = new ConcurrentHashMap<>();
    // 记录所有房间的用户id和录制文件信息
    private static Map<String, Map<Long, RecodingFile>> recordingUserAndFiles = new ConcurrentHashMap<>();

    private static OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    private ChannelData() {}

    public static Map<String, Boolean>  getChannelsRecordingStates() {
        return channelsRecordingStates;
    }

    public static Map<String, Map<Long, RecodingFile>> getRecordingUserAndFiles() {
        return recordingUserAndFiles;
    }

    public static OSS getOssClient() {
        return ossClient;
    }
}
