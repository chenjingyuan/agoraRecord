package whiteRtcRecord.whiteRtcRecord.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by az on 2018/6/17.
 */
@Service
@Slf4j
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
    @Value("${eventEndpoint}")
    private String eventEndpoint;

    private static long periodOfUpdate = 30;

    private RecordingClient recordingClient = new RecordingClient(this);
    // 用于记录房间的录制状态
    private Map<String, Boolean> channelsRecordingStates = ChannelData.getChannelsRecordingStates();
    // 记录所有房间的用户id
    private Map<String, Map<Long, RecodingFile>> channelsUserAndFiles = ChannelData.getRecordingUserAndFiles();

    private Gson gson = new Gson();

    @PostConstruct
    private void getAllUsersInfoInChannels() {
        Runnable runnable = () -> {
            List<User> users = new ArrayList<>();
            channelsUserAndFiles.keySet().stream()
                    .forEach(channelId ->
                        users.addAll(channelsUserAndFiles.get(channelId).keySet().stream()
                                .map(userId -> {
                                    User user = new User();
                                    user.setChannelId(channelId);
                                    user.setUserId(userId);
                                    user.setLastTimeInChannel(new Date());
                                    return user;
                                }).collect(Collectors.toList())
                        )
                    );
            if (users.isEmpty()) {
                return;
            }
            try {
                userInfoDAO.batchUpdateLastTimeInChannel(users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, periodOfUpdate, TimeUnit.SECONDS);
    }

    public void startRecord(String channelId, String roomToken) {
        try {
            Channel channelInfo = channelInfoDAO.getChannelByChannelId(channelId);
            if (channelInfo == null) {
                Channel channel = new Channel();
                channel.setChannelId(channelId);
                channel.setRoomToken(roomToken);
                channel.setRecordState(false);
                channelInfoDAO.addChannelInfo(channel);
            } else if (channelInfo.getRecordState() == true &&
                    channelsRecordingStates.get(channelId) != null &&
                    channelsRecordingStates.get(channelId) == true) {
                return;
            } else if (channelInfo.getRecordState() == false &&
                    channelsRecordingStates.get(channelId) != null &&
                    channelsRecordingStates.get(channelId) == true) {
                // error
                log.error("record state wrong");
                return;
            }
            log.info(System.getProperty("java.library.path"));
            String[] para = new String[] {"--appId", appId,"--uid", defaultUid,
                    "--channel", channelId ,"--appliteDir",appliteDir, "--isAudioOnly", "1",
                    "--getAudioFrame", "1", "--recordFileRootDir", "/usr/local/webapps/"
            };
            setChannelRecordingState(channelId, true);
            // 需要用sockerio跟room建连
            recordingClient.startRecord(para);
        } catch (Exception e) {
            e.printStackTrace();
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

        List<String> userIds = new ArrayList<>();
        channelsUserAndFiles.get(channelId).keySet().stream()
                .forEach(uid -> userIds.add(uid.toString()));
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("users", userIds);
        payload.put("isConnected", true);
        dispathEvent("RtcState", channelId, payload);
    }

    public void onMediaReceived(String channelId, Long userId, InputStream contentStream) {
        recorder.addRecordingChannel(channelId, userId, contentStream);
    }

    public void onUserLeave(Long userId, String channelId) throws Exception {
        if (channelsUserAndFiles.containsKey(channelId)) {
            removeChannelUserId(userId, channelId);
            List<String> userIds = new ArrayList<>();
            channelsUserAndFiles.get(channelId).keySet().stream()
                    .forEach(uid -> userIds.add(uid.toString()));

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("users", userIds);
            payload.put("isConnected", false);
            dispathEvent("RtcState", channelId, payload);

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
            User user = new User();
            user.setUserId(userId);
            user.setChannelId(channelId);
            user.setLastTimeInChannel(new Date());
            userInfoDAO.updateUserLeaveTime(user);
        } else {
            // error 用户没有加入就离开
            log.error("remove user " +userId + ", channel " + channelId +" error, user is not in channel");
        }
    }

    private void dispathEvent(String event, String channelId, Map<String, Object> payload) {
        Map<String, Object> body = new HashMap<>();
        body.put("event", event);
        body.put("payload", payload);

        RestTemplate restTemplate=new RestTemplate();
        String roomToken = channelInfoDAO.getChannelByChannelId(channelId).getRoomToken();
        String url= eventEndpoint + "?room=" + channelId + "&token=" + roomToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(body), headers);

        try {
            log.info(String.format("dispatch event %s, channelId is %s, roomToken is %s, payload is %s", event, channelId, roomToken, gson.toJson(payload)));
            log.info(String.format("dispatch event url : %s ", url));
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
    }
}
