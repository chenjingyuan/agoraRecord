package whiteRtcRecord.whiteRtcRecord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import whiteRtcRecord.whiteRtcRecord.ChannelData;
import whiteRtcRecord.whiteRtcRecord.RecodingFile;
import whiteRtcRecord.whiteRtcRecord.RecordingClient;
import whiteRtcRecord.whiteRtcRecord.dao.ChannelInfoDAO;
import whiteRtcRecord.whiteRtcRecord.dao.UserInfoDAO;
import whiteRtcRecord.whiteRtcRecord.vo.User;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by az on 2018/6/17.
 */
@Service
public class RecordingService {
    @Autowired
    private ChannelInfoDAO channelInfoDAO;
    @Autowired
    private UserInfoDAO userInfoDAO;

    @Value("${appId}")
    private String appId;
    @Value("${appliteDir}")
    private String appliteDir;
    @Value("${uid}")
    private String defaultUid;
    @Value("${libraryPath}")
    private String libraryPath;

    private RecordingClient recordingClient = new RecordingClient(this);
    // 用于记录房间的录制状态
    private Map<String, Boolean> channelsRecordingStates = ChannelData.getChannelsRecordingStates();
    // 记录所有房间的用户id
    private Map<String, Map<Long, RecodingFile>> channelsUserAndFiles = ChannelData.getRecordingUserAndFiles();

    public void startRecord(String channelId) {
        if (channelsRecordingStates.get(channelId) == true) {
            return;
        }
        // 保证一个房间只有一个录制实例, 如果进入之后就停止那么状态怎么办？
        recordingClient.stopRecord(channelId);

        System.out.println(System.getProperty("java.library.path"));
        String[] para = new String[] {"--appId", appId,"--uid", defaultUid,
                "--channel", channelId ,"--appliteDir",appliteDir, "--isAudioOnly", "1",
                "--getAudioFrame", "1", "--recordFileRootDir", "/usr/local/webapps/"
        };
        setChannelRecordingState(channelId, true);
        // 存放roomtoken
        // 需要用sockerio跟room建连
        recordingClient.startRecord(para);
    }

    private void setChannelRecordingState(String channelId, Boolean recordingState) {
        channelsRecordingStates.put(channelId, recordingState);
    }

    private void setChannelUserId(Long userId, String channelId, String path) {
        if (channelsUserAndFiles.containsKey(channelId)) {
            if (!userIsInChannel(userId, channelId)) {
                channelsUserAndFiles.get(channelId).put(userId, new RecodingFile(path));
            }
        } else {
            Map userRecordingFileMap = new ConcurrentHashMap<>();
            userRecordingFileMap.put(userId, new RecodingFile(path));
            channelsUserAndFiles.put(channelId, userRecordingFileMap);
        }
    }

    private Boolean userIsInChannel(Long userId, String channelId) {
        return channelsUserAndFiles.get(channelId).containsKey(userId);
    }

    private void removeChannelUserId(Long userId, String channelId) {
        if (channelsUserAndFiles.get(channelId).containsKey(userId)) {
            channelsUserAndFiles.get(channelId).remove(userId);
        }
    }

    public void stopRecord(String channelId) {
        recordingClient.stopRecord(channelId);
        setChannelRecordingState(channelId, false);
    }

    public void onUserJoin(Long userId, String channelId, String path) {
        setChannelUserId(userId, channelId, path);
        User user = new User();
        user.setUserId(userId);
        user.setChannelId(channelId);
        user.setJoinTime(new Date());
        user.setRecordingFilePath(path);
        userInfoDAO.setUserJoinInfo(user);
    }

    public void onUserLeave(Long userId, String channelId) throws Exception {
        if (channelsUserAndFiles.containsKey(channelId)) {
            removeChannelUserId(userId, channelId);
            if (channelsUserAndFiles.get(channelId).isEmpty()) {
                stopRecord(channelId);
            }
        } else {
            throw new Exception("user " +userId + " leave channel " + channelId +", but channel not in memory");
        }
    }

    public void uploadRecordFile() {}
}
