package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ImageDto;
import com.tss.apiservice.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
public class CommonCotroller {
    private static Logger logger = LoggerFactory.getLogger(CommonCotroller.class);

    @Value("${filePath}")
    private String filePath;

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/app/common/getBase64ByPath", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ReturnMsg getBase64ByPath(@RequestBody ImageDto imageDto) {
        ReturnMsg returnMsg;
        try {
            returnMsg = commonService.getBase64ByPath(filePath, imageDto);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/common/getBase64ByPath 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @ResponseBody
    @RequestMapping(value = "/app/common/getThumbnailImage", produces = "application/json;charset=utf-8")
    public ReturnMsg getThumbnailImage(String filePathTmp, String fileName) {
        ReturnMsg returnMsg;
        try {
            returnMsg = commonService.getThumbnailImage(filePath, filePathTmp, fileName);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/common/getThumbnailImage 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }


    @ResponseBody
    @RequestMapping(value = "/app/common/getFile", produces = "application/json;charset=utf-8")
    public void getFile(String filePathTmp, String fileName, String name, HttpServletResponse response) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            response.setContentType("application/ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode(name, "UTF-8"))));
            File file = new File(filePath + filePathTmp + fileName);
            in = new FileInputStream(file);
            out = response.getOutputStream();
            byte[] bytes = new byte[1024 * 10];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            logger.error("下载文件失败:_" + e.getMessage(), e);
        } finally {
            try {
                assert in != null;
                in.close();
                assert out != null;
                out.close();
            } catch (IOException e) {
                logger.error("下载文件失败:_" + e.getMessage(), e);
            }
        }
    }

    @RequestMapping(value = "/app/common/getNowBase64", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ReturnMsg getNowBase64(String deviceid) {
        ReturnMsg returnMsg;
        try {
            returnMsg = commonService.getNowBase64(deviceid);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/common/getNowBase64 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }
}
