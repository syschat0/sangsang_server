package kr.co.bnksys.sangsang.controller;

import kr.co.bnksys.sangsang.mapper.HireInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class HireInfoController {

    @Autowired
    HireInfoMapper hireInfoMapper;

    @GetMapping("/api/hire_info")
    public HashMap getHireInfo(@RequestParam String tableNm) {
        HashMap resultMap = new HashMap();
        
        try {
            List<HashMap> data = hireInfoMapper.selectAllFromTable(tableNm);
            resultMap.put("RESULT", "OK");
            resultMap.put("data", data);
        } catch (Exception e) {
            resultMap.put("RESULT", "ERROR");
            resultMap.put("error_msg", "테이블 조회 중 오류가 발생했습니다.");
        }
        
        return resultMap;
    }
}