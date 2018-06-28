package whiteRtcRecord.whiteRtcRecord;

import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
public class Recorder {
    private String bucket = "herewhite-test";

    private Map<String, Map<Long, RecodingFile>> recordingFiles = ChannelData.getRecordingUserAndFiles();

    /**
     * 将录制信息记录在内存中，并开始记录
     * @param channelId
     * @param userId
     * @param contentStream
     */
    // 该方法应该使用线程池异步进行，或者调用异步上传接口
    public void addRecordingChannel(String channelId, Long userId, InputStream contentStream) {
        if (recordingFiles.get(channelId) == null || recordingFiles.get(channelId).get(userId) == null) {
            /**
             * error 用户不在房间时不应该收到回调
             * 用户离开房间后立即删除用户数据，有可能会丢失数据
             * 但是由于无法知道用户离开后数据是否传完，还需要完善方案
             */
            log.info("user " + userId + " has levave channel " + channelId);
        } else {
            RecodingFile recodingFile =  recordingFiles.get(channelId).get(userId);

            if (recodingFile.appendObjectRequest == null) {
                log.info("write to " + recodingFile.path);
                buildAndSaveRecordFile(recodingFile, contentStream);
            } else {
                AppendObjectRequest appendRequest = recodingFile.appendObjectRequest;
                appendRequest.setInputStream(contentStream);
                recodingFile.uploadPosition = appendUpload(appendRequest, recodingFile.uploadPosition);
            }
        }
    }

    private RecodingFile buildAndSaveRecordFile(RecodingFile recodingFile, InputStream contentStream) {
        ObjectMetadata meta = new ObjectMetadata();
        // 指定上传的内容类型。
        meta.setContentType("text/plain");
        // 可通过AppendObjectRequest方法设置参数。
        AppendObjectRequest appendObjectRequest =
                new AppendObjectRequest(bucket, recodingFile.path, contentStream, meta);

        recodingFile.uploadPosition = appendUpload(appendObjectRequest, 0L);
        recodingFile.appendObjectRequest = appendObjectRequest;
        return recodingFile;
    }

    private Long appendUpload(AppendObjectRequest appendObjectRequest, Long position) {
        appendObjectRequest.setPosition(position);
        AppendObjectResult appendObjectResult = ChannelData.getOssClient().appendObject(appendObjectRequest);
        //Object的64位CRC值。此值是根据[ECMA-182]标准计算得出。
//        System.out.println("================" + appendObjectResult.getObjectCRC());
        return appendObjectResult.getNextPosition();
    }

}
