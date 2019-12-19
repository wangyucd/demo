package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuangyu
 * @date 2019/12/13
 */
@Configuration
public class MainConfigration {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    JSR250Bean jsr250Bean(){
        return new JSR250Bean();
    }
}
