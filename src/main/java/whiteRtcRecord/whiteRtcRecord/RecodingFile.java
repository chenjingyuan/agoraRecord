package whiteRtcRecord.whiteRtcRecord;

import com.aliyun.oss.model.AppendObjectRequest;

/**
 * Created by az on 2018/6/20.
 */
public class RecodingFile {
    String path;
    Long uploadPosition;
    AppendObjectRequest appendObjectRequest;

    public RecodingFile(String path) {
        this.path = path;
    }
}