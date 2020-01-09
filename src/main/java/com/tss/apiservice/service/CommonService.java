package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.ImageDto;

import java.io.IOException;

public interface CommonService {
    ReturnMsg getBase64ByPath(String filePath, ImageDto imageDto) throws Exception;

    ReturnMsg getThumbnailImage(String filePath, String filePathTmp, String fileName) throws Exception;

    ReturnMsg getNowBase64(String deviceid) throws Exception;
}
