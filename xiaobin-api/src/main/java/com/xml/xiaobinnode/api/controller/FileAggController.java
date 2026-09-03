package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.community.CommunityFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 聚合-文件上传
 */
@Tag(name = "聚合-文件上传")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileAggController {

    private final CommunityFeignClient communityFeignClient;

    @PostMapping("/upload")
    @Operation(summary = "单文件上传")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(communityFeignClient.uploadFile(file));
    }

    @PostMapping("/upload-batch")
    @Operation(summary = "批量上传")
    public Result<List<String>> uploadBatch(@RequestParam("files") List<MultipartFile> files) {
        return Result.success(communityFeignClient.uploadFiles(files));
    }
}
