package com.example.demo;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

public class JSR250Bean {
    private String messageString;
    
    @Resource(name="jSR250Bean")
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
}