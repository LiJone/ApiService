package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CommonServiceImpl implements CommonService {
    @Override
    public ReturnMsg getBase64ByPath(String filePath, String filePathImp, String fileName) throws Exception {
        ReturnMsg<Object> returnMsg = new ReturnMsg<>(ReturnMsg.FAIL, "失敗");
        if (StringUtils.isEmpty(filePathImp) || StringUtils.isEmpty(fileName)) {
            returnMsg.setMsgbox("參數異常...");
        } else {
            String base64File = com.tss.apiservice.common.utils.FilesUtils.encodeBase64File(filePath + filePathImp + fileName);
            returnMsg = new ReturnMsg<>(ReturnMsg.SUCCESS, "成功", base64File);
        }
        return returnMsg;
    }
}
