package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;

public interface CommonService {
    ReturnMsg getBase64ByPath(String filePath,String filePathTmp , String fileName, String imageIndex, String name) throws Exception;

    ReturnMsg getThumbnailImage(String filePath, String filePathTmp, String fileName) throws Exception;
}
