package com.example.demo.dao;

import com.example.demo.entity.OrderDelayTracking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDelayTrackingMapper {

    List<OrderDelayTracking> getByOrderId(Long orderId);

    int insert(OrderDelayTracking record);

}