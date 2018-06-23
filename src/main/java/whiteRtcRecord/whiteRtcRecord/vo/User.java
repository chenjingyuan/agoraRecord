package whiteRtcRecord.whiteRtcRecord.vo;

import java.util.Date;

/**
 * Created by az on 2018/6/16.
 */
public class User {
    private Long userId;
    private String channelId;
    private Date joinTime;
    private Date lastTimeInChannel;
    private String recordingFilePath;
    private Boolean leaved;

    public String getRecordingFilePath() {
        return recordingFilePath;
    }

    public void setRecordingFilePath(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getLastTimeInChannel() {
        return lastTimeInChannel;
    }

    public void setLastTimeInChannel(Date lastTimeInChannel) {
        this.lastTimeInChannel = lastTimeInChannel;
    }

    public Boolean getLeaved() {
        return leaved;
    }

    public void setLeaved(Boolean leaved) {
        this.leaved = leaved;
    }
}
