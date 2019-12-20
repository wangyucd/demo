package com.example.demo.property;

import lombok.Data;

import java.util.List;

@Data
public class People {

    private String name;

    private Integer age;

    private List<String> address;

}
