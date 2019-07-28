package com.tss.apiservice.service;

import com.tss.apiservice.common.ReturnMsg;
import com.tss.apiservice.dto.UsersDto;
import com.tss.apiservice.dto.UsersSetDto;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface UsersService {
    ReturnMsg userLogin(String username, String password);

    ReturnMsg getUserList(HttpServletRequest request);

    ReturnMsg addUser(String userid, UsersDto usersDto);

    ReturnMsg updateUser(String userid, UsersDto usersDto);

    ReturnMsg deleteUser(String userid, UsersDto usersDto);

    ReturnMsg getUserPower(HttpServletRequest request);

    ReturnMsg getUserSetting(HttpServletRequest request);

    ReturnMsg setUserSetting(String userid, UsersSetDto usersSetDto) throws ParseException;
}
