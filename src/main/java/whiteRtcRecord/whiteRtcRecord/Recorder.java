package whiteRtcRecord.whiteRtcRecord;

import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Recorder {
    @Value("${bucket}")
    private String bucket = "herewhite-test";

    private Uploader uploader = new Uploader();

    private Map<String, List<RecodingFile>> recordingFiles = ChannelData.getRecordingFiles();

    // 将录制信息记录在内存中，并开始记录
    public void addRecordingChannel(String channelName, Long userId, InputStream contentStream) {
        if (recordingFiles.get(channelName) == null) {
            List<RecodingFile> recodingFiles = new ArrayList<>();

            recodingFiles.add(buildRecordFile(fileName, contentStream));

            recordingFiles.put(channelName, recodingFiles);
        } else {
            // 由于一个channel中的user不会重复，所以user的录制文件也是唯一的
            List<RecodingFile> file =
                    recordingChannel.get(channelName).stream().filter(recodingFile -> {
                        System.out.println("!!!!!!!!!!!!!!!" + recodingFile.name + "///" + fileName);
                        return recodingFile.name.equals(fileName);
                    }).collect(Collectors.toList());
            System.out.println("++++++++++++++" + file.size());

            if (file.size() == 0) {
                recordingChannel.get(channelName).add(buildRecordFile(fileName,contentStream));
            } else {
                AppendObjectRequest appendRequest = file.get(0).appendObjectRequest;
                appendRequest.setInputStream(contentStream);
                file.get(0).uploadPosition = uploader.appendUpload(appendRequest, file.get(0).uploadPosition);
            }
        }
    }

    public RecodingFile buildRecordFile(String fileName, InputStream contentStream) {
        RecodingFile recodingFile = new RecodingFile(fileName);

        ObjectMetadata meta = new ObjectMetadata();
        // 指定上传的内容类型。
        meta.setContentType("text/plain");
        //可通过AppendObjectRequest方法设置参数。
        AppendObjectRequest appendObjectRequest =
                new AppendObjectRequest(bucket, fileName, contentStream, meta);

        recodingFile.uploadPosition = uploader.appendUpload(appendObjectRequest, 0L);
        recodingFile.appendObjectRequest = appendObjectRequest;
        System.out.println("~~~~~~~~~~~~" + recodingFile + " //// " + fileName);
        return recodingFile;
    }

}
