package com.tss.apiservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ImageDto;
import com.tss.apiservice.service.CommonService;
import com.tss.apiservice.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 壮Jone
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final String THUMBNAIL = "_thumbnail";

    @Value("${loginUrl}")
    private String loginUrl;

    @Value("${serverIpUrl}")
    private String serverIpUrl;

    @Value("${getImageUrl}")
    private String getImageUrl;

    @Value("${loginName}")
    private String loginName;

    @Value("${passWord}")
    private String passWord;

    @Value("${serverip}")
    private String serverip;

    @Value("${deviceid}")
    private String deviceid;

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
    public ReturnMsg getNowBase64(String deviceid) throws Exception {
        ReturnMsg returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        String url = getImageUrl + "?deviceid=" + this.deviceid + "&serverip=" + serverip;
        String s = HttpUtils.doGet(url);
        if (!StringUtils.isEmpty(s)) {
            JSONObject jsonObject = JSON.parseObject(s);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                Map<String, String> map = new HashMap<>(1);
                map.put("fileData", jsonObject.getString("picBase64"));
                returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", map);
            } else if ("202".equals(status)){
                returnMsg.setMsgbox("设备不在线！");
            } else {
                returnMsg.setMsgbox("抓取返回失败！");
            }
        } else {
            returnMsg.setMsgbox("第三方接口返回为空");
        }
        return returnMsg;
    }
}
