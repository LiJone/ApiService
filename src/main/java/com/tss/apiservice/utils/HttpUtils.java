package com.tss.apiservice.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

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

    public static String doGet(String url) {
        // 输入流
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        // 创建httpClient实例
        HttpClient httpClient = new HttpClient();
        // 设置http连接主机服务超时时间：15000毫秒
        // 先获取连接管理器对象，再获取参数对象,再进行参数的赋值
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建一个Get方法实例对象
        GetMethod getMethod = new GetMethod(url);
        // 设置get请求超时为60000毫秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        // 设置请求重试机制，默认重试次数：3次，参数设置为true，重试机制可用，false相反
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
        try {
            // 执行Get方法
            int statusCode = httpClient.executeMethod(getMethod);
            // 判断返回码
            if (statusCode != HttpStatus.SC_OK) {
                // 如果状态码返回的不是ok,说明失败了,打印错误信息
                System.err.println("Method faild: " + getMethod.getStatusLine());
            } else {
                // 通过getMethod实例，获取远程的一个输入流
                is = getMethod.getResponseBodyAsStream();
                // 包装输入流
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                StringBuffer sbf = new StringBuffer();
                // 读取封装的输入流
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp).append("\r\n");
                }

                result = sbf.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放连接
            getMethod.releaseConnection();
        }
        return result;
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
