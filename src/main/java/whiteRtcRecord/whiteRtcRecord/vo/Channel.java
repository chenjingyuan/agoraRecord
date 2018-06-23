package whiteRtcRecord.whiteRtcRecord.vo;

/**
 * Created by az on 2018/6/16.
 */
public class Channel {
    private String channelId;
    private String roomToken;
    private Boolean recordState;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getRoomToken() {
        return roomToken;
    }

    public void setRoomToken(String roomToken) {
        this.roomToken = roomToken;
    }

    public Boolean getRecordState() {
        return recordState;
    }

    public void setRecordState(Boolean recordState) {
        this.recordState = recordState;
    }
}
