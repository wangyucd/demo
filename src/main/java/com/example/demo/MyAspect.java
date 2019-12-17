package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * ClassName: MyAspect
 * Function:  TODO
 * Date:      2019/12/17 16:04
 * @author shuangyu
 */
@Slf4j
@Aspect
public class MyAspect {

    @Pointcut("execution(public * com.example.demo.controller.*.*(..))")
    public void controller() {

    }

    @Before("controller()")
    public void before(JoinPoint jp) {
        log.info("before,{}", jp);

    }

    @After("controller()")
    public void after(JoinPoint jp) {
        log.info("before,{}", jp);
    }

    @AfterReturning(pointcut = "controller()", returning = "ret")
    public void afterReturning(Object ret) {
        log.info("afterReturning,{}", ret);
    }

    @AfterThrowing(pointcut = "controller()", throwing = "throwable")
    public void afterThrowing(JoinPoint jp, Throwable throwable) {
        log.info("afterThrowing,{},throwing:{}", jp, throwable);
    }

    @Around("controller()")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            Object o = pjp.proceed();
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
