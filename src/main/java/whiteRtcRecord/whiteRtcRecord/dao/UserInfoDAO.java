package whiteRtcRecord.whiteRtcRecord.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whiteRtcRecord.whiteRtcRecord.vo.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by az on 2018/6/17.
 */
@Service
public class UserInfoDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setUserJoinInfo(User user) {
        jdbcTemplate.update(
                "INSERT INTO recording_user_info (user_id, channel_id, join_time, recording_file_path, leaved)" +
                        " VALUES (?, ?, ?, ?, ?)",
                user.getUserId(), user.getChannelId(), new Timestamp(user.getJoinTime().getTime()), user.getRecordingFilePath(), user.getLeaved()
        );
    }

    public void updateUserLeaveTime(User user) {
        jdbcTemplate.update(
                "UPDATE recording_user_info SET last_time_in_channel = ?, leaved = ? " +
                        " WHERE user_id = ? AND channel_id = ?",
                new Timestamp(user.getLastTimeInChannel().getTime()), true, user.getUserId(), user.getChannelId()
        );
    }

    /**
     * 根据userId和chanelId更新用户最后在房间的时间
     * @param users
     */
    public void batchUpdateLastTimeInChannel(List<User> users) {
        String sql = "UPDATE recording_user_info SET " +
                "last_time_in_channel= ? WHERE user_id = ? AND channel_id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return users.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)throws SQLException {
                User user = users.get(i);
                ps.setTimestamp(1, new Timestamp(user.getLastTimeInChannel().getTime()));
                ps.setLong(2, user.getUserId());
                ps.setString(3, user.getChannelId());
            }
        });
    }
}
