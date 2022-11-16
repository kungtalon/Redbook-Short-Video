package org.talon.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talon.datautils.MinIOUtils;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Configuration
@Data
public class MinIOConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.fileHost}")
    private String fileHost;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.imgSizeLimit}")
    private Integer imgSizeLimit;

    @Value("${minio.fileSizeLimit}")
    private Integer fileSizeLimit;

    @Bean
    public MinIOUtils createMinioClient() {
        return new MinIOUtils(endpoint, bucketName, accessKey, secretKey, imgSizeLimit, fileSizeLimit);
    }
}
