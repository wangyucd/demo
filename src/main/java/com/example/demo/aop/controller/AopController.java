package com.example.demo.aop.controller;

import com.example.demo.dao.OrderDelayTrackingMapper;
import com.example.demo.entity.OrderDelayTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: AopController
 * Function:  TODO
 * Date:      2019/12/9 15:10
 * @author shuangyu
 */
@RestController
public class AopController {


    @RequestMapping("aop")
    public String aop(String a) {
        return "aop"+a;
    }

}
