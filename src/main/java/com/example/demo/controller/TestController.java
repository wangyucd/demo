package com.example.demo.controller;

import com.example.demo.dao.OrderDelayTrackingMapper;
import com.example.demo.entity.OrderDelayTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Function:  TODO
 * Date:      2019/12/9 15:10
 * @author shuangyu
 */
@RestController
public class TestController {

    @Autowired
    OrderDelayTrackingMapper orderDelayTrackingMapper;

    @RequestMapping("get")
    public String get() {
        return orderDelayTrackingMapper.getByOrderId(271L).size() + "";
    }

    @RequestMapping("insert")
    public String insert() {
        OrderDelayTracking s = orderDelayTrackingMapper.getByOrderId(271L).get(0);
        s.setTrackingId(null);
        return orderDelayTrackingMapper.insert(s) + "";
    }
}
