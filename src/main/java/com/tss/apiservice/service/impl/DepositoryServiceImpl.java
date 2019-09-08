package com.tss.apiservice.service.impl;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dao.DepositoryMapper;
import com.tss.apiservice.service.DepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * @author 壮Jone
 */
@Service
public class DepositoryServiceImpl implements DepositoryService {
    @Autowired
    private DepositoryMapper depositoryMapper;

    @Override
    public ReturnMsg<Object> getDepositoryMsgList(HttpServletRequest request) throws ParseException {
        return null;
    }
}
