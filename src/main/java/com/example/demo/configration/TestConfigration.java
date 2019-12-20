package com.example.demo.configration;

import com.example.demo.property.People;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: TestConfigration
 * Function:  TODO
 * Date:      2019/12/19 14:46
 * @author shuangyu
 */
@Configuration
public class TestConfigration {
    @Bean
    @ConfigurationProperties(prefix = "com.example.demo")
     People people() {

        return new People();
    }
}
