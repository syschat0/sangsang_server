package kr.co.bnksys.sangsang.controller;


import jakarta.servlet.http.HttpSession;
import kr.co.bnksys.sangsang.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController

public class UserController {


    @Autowired
    UserMapper userMapper;

    @PostMapping("/user/Login")
    public HashMap fn_Login(@RequestBody HashMap paramMap, HttpSession session){
        HashMap resultMap=new HashMap();
        System.out.println(paramMap.toString());
        HashMap loginMap = userMapper.selectUserLogin(paramMap);

        if ( loginMap == null){
            resultMap.put("RESULT","NO");
        }else{
            resultMap.put("RESULT","OK");
            resultMap.putAll(loginMap);

            session.setAttribute("loginUser",resultMap);

        }
        return resultMap;
    }

    @PostMapping("/user/Register")
    public HashMap fn_Register(@RequestBody HashMap paramMap){
        HashMap resultMap = new HashMap();

        System.out.println("paramMap : " + paramMap.toString());

        // 사용자 중복 체크
        String strDupYn = userMapper.selectUserDupChk(paramMap);

        if ( "Y".equals(strDupYn) ){
            resultMap.put("RESULT","error");
            resultMap.put("error_msg","이메일 중복입니다.");
        }else{
            try {
                userMapper.insertUserRegister(paramMap);
            }catch(Exception e){
                resultMap.put("RESULT","error");
                resultMap.put("error_msg","오류가 발생했습니다. 관리자에게 문의하세요");
                return resultMap;
            }
            resultMap.put("RESULT","success");
        }
        return resultMap;
    }



    @GetMapping("/user/SessionInfo")
    public HashMap fn_SessionInfo(HttpSession session) {
        HashMap resultMap = new HashMap();

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            resultMap.put("RESULT", "NO_SESSION");
        } else {
            resultMap.put("RESULT", "OK");
            resultMap.put("USER", loginUser);
        }

        return resultMap;
    }

    @PostMapping("user/Logout")
    public HashMap fn_Logout(HttpSession session) {
        session.invalidate();
        HashMap resultMap = new HashMap();
        resultMap.put("RESULT", "LOGOUT");
        return resultMap;
    }




}
