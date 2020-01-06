package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ImageDto;
import com.tss.apiservice.service.CommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 壮Jone
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final String THUMBNAIL = "_thumbnail";

//    @Value("${filePath}")
//    private String filePath;
//
//    @Value("${filePath}")
//    private String filePath;
//
//    @Value("${filePath}")
//    private String filePath;
//
//    @Value("${filePath}")
//    private String filePath;

    @Override
    public ReturnMsg getBase64ByPath(String filePath, ImageDto imageDto) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(imageDto.getFilePathTmp()) || StringUtils.isEmpty(imageDto.getFileName())) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            String base64File = com.tss.apiservice.common.utils.FilesUtils.encodeBase64File(filePath + imageDto.getFilePathTmp() + imageDto.getFileName());
            Map<String, String> map = new HashMap<>(5);
            map.put("fileData", base64File);
            map.put("filePathTmp", imageDto.getFilePathTmp());
            map.put("fileName", imageDto.getFileName());
            map.put("imageIndex", imageDto.getImageIndex());
            map.put("name", imageDto.getName());
            returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", map);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getThumbnailImage(String filePath, String filePathTmp, String fileName) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(filePathTmp) || StringUtils.isEmpty(fileName)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            if (fileName.contains(THUMBNAIL)) {
                fileName = fileName.replace(THUMBNAIL, "");
            }
            String base64File = com.tss.apiservice.common.utils.FilesUtils.encodeBase64File(filePath + filePathTmp + fileName);
            Map<String, String> map = new HashMap<>(3);
            map.put("fileData", base64File);
            map.put("filePathImp", filePathTmp);
            map.put("fileName", fileName);
            returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", map);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getNowBase64(String deviceid) {
        return null;
    }
}
