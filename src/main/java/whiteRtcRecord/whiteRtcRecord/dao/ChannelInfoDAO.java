package whiteRtcRecord.whiteRtcRecord.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whiteRtcRecord.whiteRtcRecord.vo.Channel;

import java.util.List;

/**
 * Created by az on 2018/6/16.
 */
@Service
public class ChannelInfoDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Channel> getChannels(){
        String sql = "SELECT channel_id, room_token, record_state FROM recording_channel_info";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Channel channel = new Channel();
            channel.setChannelId(rs.getString("channel_id"));
            channel.setRoomToken(rs.getString("room_token"));
            channel.setRecordState(rs.getBoolean("record_state"));
            return channel;
        });
    }

    public Channel getChannelByChannelId(String channelId){
        String sql = "SELECT channel_id, room_token, record_state FROM recording_channel_info WHERE channel_id = ?";
        return jdbcTemplate.query(sql, (rs) -> {
            if (rs.next()) {
                Channel channel = new Channel();
                channel.setChannelId(rs.getString("channel_id"));
                channel.setRoomToken(rs.getString("room_token"));
                channel.setRecordState(rs.getBoolean("record_state"));
                return channel;
            } else {
                return null;
            }
        }, channelId);
    }

    public void addChannelInfo(Channel channel) {
        jdbcTemplate.update(
                "INSERT INTO recording_channel_info (channel_id, room_token, record_state)" +
                        " VALUES (?, ?, ?)",
                channel.getChannelId(), channel.getRoomToken(), channel.getRecordState()
        );
    }

    public void updateChannelState(String channelId, Boolean recordState) {
        jdbcTemplate.update(
                "UPDATE recording_channel_info SET record_state = ? WHERE channel_id = ?",
                recordState, channelId
        );
    }
}
