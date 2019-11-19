package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.service.CommonService;
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

    @Override
    public ReturnMsg getBase64ByPath(String filePath, String filePathTmp, String fileName, String imageIndex, String name) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(filePathTmp) || StringUtils.isEmpty(fileName)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            String base64File = com.tss.apiservice.common.utils.FilesUtils.encodeBase64File(filePath + filePathTmp + fileName);
            Map<String, String> map = new HashMap<>(5);
            map.put("fileData", base64File);
            map.put("filePathTmp", filePathTmp);
            map.put("fileName", fileName);
            map.put("imageIndex", imageIndex);
            map.put("name", name);
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
}
