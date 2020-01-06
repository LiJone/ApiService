package com.tss.apiservice.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * @author 壮Jone
 */
public class HttpUtils {

    public static String sendPost(String params, String requestUrl) throws IOException {

        // 将参数转为二进制流
        byte[] requestBytes = params.getBytes(StandardCharsets.UTF_8);
        // 客户端实例化
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(requestUrl);
        // 设置请求头  Content-Type
        postMethod.setRequestHeader("Content-Type", "application/json");
        InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
                requestBytes.length);
        RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
                // 请求体
                requestBytes.length, "application/json; charset=utf-8");
        postMethod.setRequestEntity(requestEntity);
        // 执行请求
        httpClient.executeMethod(postMethod);
        // 获取返回的流
        InputStream soapResponseStream = postMethod.getResponseBodyAsStream();
        byte[] datas = null;
        try {
            // 从输入流中读取数据
            datas = readInputStream(soapResponseStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将二进制流转为String
        return new String(datas, StandardCharsets.UTF_8);
    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream (InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
}
