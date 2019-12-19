package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuangyu
 * @date 2019/12/13
 */
@Configuration
public class InnerConfigration {

    @Configuration
     class Inner {

        @Bean
        SomeBean someBean() {
            return new SomeBean();
        }
    }
}
