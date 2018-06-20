package whiteRtcRecord.whiteRtcRecord.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whiteRtcRecord.whiteRtcRecord.vo.User;

/**
 * Created by az on 2018/6/17.
 */
@Service
public class UserInfoDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setUserJoinInfo(User user) {
        jdbcTemplate.update(
                "INSERT INTO recording_user_info (user_id, channel_id, join_time, recording_file_path)" +
                        " VALUES (?, ?, ?, ?)",
                user.getUserId(), user.getChannelId(), user.getJoinTime(), user.getRecordingFilePath()
        );
    }

    public void updateUserLeaveTime(User user) {
        jdbcTemplate.update(
                "UPDATE recording_user_info SET leave_time = ?" +
                        " WHERE user_id = ? AND channel_id = ?",
                user.getLeaveTime(), user.getUserId(), user.getChannelId()
        );
    }
}
