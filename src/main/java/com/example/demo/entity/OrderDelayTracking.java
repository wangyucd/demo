package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OrderDelayTracking {

    private Integer trackingId;

    private Long orderId;

    private String orderDelayType;

    private Integer delayDays;

    private String operType;

    private String createdBy;

    private Date createdWhen;

}