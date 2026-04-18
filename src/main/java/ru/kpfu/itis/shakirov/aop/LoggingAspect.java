package ru.kpfu.itis.shakirov.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAspect {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* ru.kpfu.itis.shakirov.*.*(..)) && !within(ru.kpfu.itis.shakirov.config.properties..*)")
    public void logExecution() {

    }

    @Around("logExecution()")
    public Object log(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getName();
        LOGGER.info("Start execution {}.{}", className, methodName);
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        LOGGER.info("End execution {}.{}", className, methodName);
        return result;
    }
}
