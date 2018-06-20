package whiteRtcRecord.whiteRtcRecord;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Created by az on 2018/6/9.
 */
@Component
public class Uploader {
    @Value("${endpoint}")
    static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    @Value("${accessKeyId}")
    static String accessKeyId = "LTAIdIgc3ScCo8Xy";
    @Value("${accessKeySecret}")
    static String accessKeySecret = "efeIceFOCANGpfFAypte7UnGe1VkY8";
    @Value("${bucket}")
    static String bucket;

    private OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    public Long firstUpload(String fileName, InputStream contentStream) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/plain");
        AppendObjectRequest appendObjectRequest =
                new AppendObjectRequest(bucket, fileName, contentStream, meta);
        appendObjectRequest.setPosition(0L);
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        return appendObjectResult.getNextPosition();
    }

    public Long appendUpload(AppendObjectRequest appendObjectRequest, Long position) {
        appendObjectRequest.setPosition(position);
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        //Object的64位CRC值。此值是根据[ECMA-182]标准计算得出。
        System.out.println("================" + appendObjectResult.getObjectCRC());
        return appendObjectResult.getNextPosition();
    }

    public void uploadDone() {
        ossClient.shutdown();
    }
}
