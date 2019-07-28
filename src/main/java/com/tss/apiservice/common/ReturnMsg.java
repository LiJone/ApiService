package com.tss.apiservice.common;

import java.io.Serializable;

public class ReturnMsg<T> implements Serializable {
    private static final long serialVersionUID = 6589008794478961923L;
    public static final int SUCCESS = 1;
    public static final int DATA_NULL = 10;
    public static final int FAIL = 0;
    private int code;
    private String msgbox;
    private T data;

    public ReturnMsg() {
    }

    public ReturnMsg(int code, String msgbox, T data) {
        this.code = code;
        this.msgbox = msgbox;
        this.data = data;
    }

    public ReturnMsg(int code, String msgbox) {
        this.code = code;
        this.msgbox = msgbox;
    }

    public ReturnMsg(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public String getMsgbox() {
        return this.msgbox;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
