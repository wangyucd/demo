package com.example.demo;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Data
public class SomeBean {
    private String name;
    
}