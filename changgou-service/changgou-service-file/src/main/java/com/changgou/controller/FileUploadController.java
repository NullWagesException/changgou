package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.utils.FastDFSUtils;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Author: nullWagesException
 * @Date: 2019/12/28 1:49
 * @Description:
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUploadController {

    @PostMapping
    public Result upload(@RequestParam("file")MultipartFile file) throws Exception{
        //封装文件信息
        FastDFSFile fastDFSFile = new FastDFSFile(
            file.getOriginalFilename(),//文件名字
                file.getBytes(),//文件内容
                StringUtils.getFilenameExtension(file.getOriginalFilename())//文件扩展名
        );


        String[] upload = FastDFSUtils.upload(fastDFSFile);

        String url = FastDFSUtils.getTrackerUrl() + "/" + upload[0] + "/" + upload[1];
        return new Result<>(true, StatusCode.OK,"上传成功",url);
    }

}
