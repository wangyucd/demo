package com.example.demo;


import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.logging.Logger;

@Slf4j
public class JSR250Bean {
    private String messageString;
    
    public void setMessage(String message){
        this.messageString = message;
    }
    
    public void getMessage(){
        System.out.println(this.messageString);
    }
    
    @PostConstruct
    public void initPost(){
        System.out.println("@ init");
    }
    
    @PreDestroy
    public void destroyPre(){
        System.out.println("@ destroy");
    }

    public  void init(){
        log.info("my init");
    }

    public  void destroy(){
        log.info("my destroy");
    }
}