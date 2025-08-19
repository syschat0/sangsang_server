package kr.co.bnksys.sangsang.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface UserMapper {

    String selectUserDupChk(HashMap paramMap);
    void insertUserRegister(HashMap paramMap);

    HashMap selectUserLogin(HashMap paramMap);
}
