package com.tss.apiservice.controller;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class CommonCotroller {
    private static Logger logger = LoggerFactory.getLogger(CommonCotroller.class);

    @Value("${filePath}")
    private String filePath;

    @Autowired
    CommonService commonService;

    @RequestMapping(value = "/app/common/getBase64ByPath", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMsg getBase64ByPath(String filePathTmp, String fileName, String imageIndex, String name) {
        ReturnMsg returnMsg;
        try {
            returnMsg = commonService.getBase64ByPath(filePath, filePathTmp, fileName, imageIndex, name);
        } catch (Exception e) {
            returnMsg = new ReturnMsg(ReturnMsg.FAIL, "未獲取到相關數據......");
            logger.info("/app/common/getBase64ByPath 异常");
            e.printStackTrace();
        }
        return returnMsg;
    }

    @ResponseBody
    @RequestMapping(value = "/app/common/getThumbnailImage", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
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
    @RequestMapping(value = "/app/common/getImage", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public void getImage(String filePathTmp, String fileName, HttpServletResponse response) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
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
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
