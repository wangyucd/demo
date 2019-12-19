package com.example.demo.java.base.jdk.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author shuangyu
 * @date 2019/12/13
 */
public class Cas {
   volatile static int aa=0;

    public static void main(String[] args){

int c=1;
        IntStream.range(1,3).forEach(a->{
           new Thread(
                   ()->{
                       IntStream.range(1,101).forEach(b->{
                           System.out.println(c);
                       });
                   }

           ).start();
        });
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(aa);
    }
}
