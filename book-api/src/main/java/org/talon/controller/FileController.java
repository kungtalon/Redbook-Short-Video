package org.talon.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.talon.config.MinIOConfig;
import org.talon.datautils.MinIOUtils;
import org.talon.rest.JsonResponse;

import java.io.IOException;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@Slf4j
@RequestMapping("file")
@RestController
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;

    @ApiOperation(value = "hello - test api")
    @PostMapping("upload")
    public JsonResponse upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());

        String imgUrl = String.join("/",
                minIOConfig.getFileHost(),
                minIOConfig.getBucketName(),
                fileName);

        return JsonResponse.ok(imgUrl);
    }
}
