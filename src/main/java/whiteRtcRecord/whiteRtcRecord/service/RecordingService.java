package whiteRtcRecord.whiteRtcRecord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import whiteRtcRecord.whiteRtcRecord.ChannelData;
import whiteRtcRecord.whiteRtcRecord.RecodingFile;
import whiteRtcRecord.whiteRtcRecord.Recorder;
import whiteRtcRecord.whiteRtcRecord.RecordingClient;
import whiteRtcRecord.whiteRtcRecord.dao.ChannelInfoDAO;
import whiteRtcRecord.whiteRtcRecord.dao.UserInfoDAO;
import whiteRtcRecord.whiteRtcRecord.vo.Channel;
import whiteRtcRecord.whiteRtcRecord.vo.User;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by az on 2018/6/17.
 */
@Service
public class RecordingService {
    @Autowired
    private ChannelInfoDAO channelInfoDAO;
    @Autowired
    private UserInfoDAO userInfoDAO;
    @Autowired
    private Recorder recorder;


    @Value("${appId}")
    private String appId;
    @Value("${appliteDir}")
    private String appliteDir;
    @Value("${uid}")
    private String defaultUid;
    @Value("${libraryPath}")
    private String libraryPath;

    private static long periodOfUpdate = 30;

    private RecordingClient recordingClient = new RecordingClient(this);
    // 用于记录房间的录制状态
    private Map<String, Boolean> channelsRecordingStates = ChannelData.getChannelsRecordingStates();
    // 记录所有房间的用户id
    private Map<String, Map<Long, RecodingFile>> channelsUserAndFiles = ChannelData.getRecordingUserAndFiles();

    @PostConstruct
    private void getAllUsersInfoInChannels() {
        Runnable runnable = () -> {
            List<User> users = new ArrayList<>();
            channelsUserAndFiles.keySet().stream()
                    .forEach(channelId -> {
                        users.addAll(channelsUserAndFiles.get(channelId).keySet().stream()
                                .map(userId -> {
                                    User user = new User();
                                    user.setChannelId(channelId);
                                    user.setUserId(userId);
                                    user.setLastTimeInChannel(new Date());
                                    return user;
                                }).collect(Collectors.toList())
                        );
                    });
            if (users.isEmpty()) {
                return;
            }
            userInfoDAO.batchUpdateLastTimeInChannel(users);
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, periodOfUpdate, TimeUnit.SECONDS);
    }

    public void startRecord(String channelId, String roomToken) {
        Channel channelInfo = channelInfoDAO.getChannelByChannelId(channelId);
        if (channelInfo == null) {
            Channel channel = new Channel();
            channel.setChannelId(channelId);
            channel.setRoomToken(roomToken);
            channel.setRecordState(false);
            channelInfoDAO.addChannelInfo(channel);
        }
        if (channelInfo.getRecordState() == true && channelsRecordingStates.get(channelId) == true) {
            return;
        } else if (channelInfo.getRecordState() == false && channelsRecordingStates.get(channelId) == true) {
            // error
            System.out.println("record state wrong");
        } else {
            recordingClient.stopRecord(channelId);

            System.out.println(System.getProperty("java.library.path"));
            String[] para = new String[] {"--appId", appId,"--uid", defaultUid,
                    "--channel", channelId ,"--appliteDir",appliteDir, "--isAudioOnly", "1",
                    "--getAudioFrame", "1", "--recordFileRootDir", "/usr/local/webapps/"
            };
            setChannelRecordingState(channelId, true);
            // 需要用sockerio跟room建连
            recordingClient.startRecord(para);
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
        user.setLeaved(false);
        userInfoDAO.setUserJoinInfo(user);
    }

    public void onMediaReceived(String channelId, Long userId, InputStream contentStream) {
        recorder.addRecordingChannel(channelId, userId, contentStream);
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

    private void setChannelRecordingState(String channelId, Boolean recordingState) {
        channelsRecordingStates.put(channelId, recordingState);
        channelInfoDAO.updateChannelState(channelId, recordingState);
    }

    private void setChannelUserId(Long userId, String channelId, String path) {
        if (channelsUserAndFiles.containsKey(channelId)) {
            if (!userIsInChannel(userId, channelId)) {
                channelsUserAndFiles.get(channelId).put(userId, new RecodingFile(path));
            }
        } else {
            Map<Long, RecodingFile> userRecordingFileMap = new ConcurrentHashMap<>();
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
        } else {
            // error 用户没有加入就离开
            System.out.println("remove user " +userId + ", channel " + channelId +" error, user is not in channel");
        }
    }
}
