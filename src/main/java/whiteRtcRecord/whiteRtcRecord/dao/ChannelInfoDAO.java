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
        String sql = "SELECT channel_id, room_token FROM recording_channel_info";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Channel channel = new Channel();
            channel.setChannelId(rs.getString("channel_id"));
            channel.setRoomToken(rs.getString("room_token"));
            return channel;
        });
    }

    public void setChannelInfo(Channel channel) {
        jdbcTemplate.update(
                "INSERT INTO recording_channel_info (channel_id, room_token)" +
                        " VALUES (?, ?, ?)",
                channel.getChannelId(), channel.getRoomToken()
        );
    }
}
