package ru.kpfu.itis.shakirov.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.shakirov.aop.annotation.Metric;
import ru.kpfu.itis.shakirov.service.MetricsService;

import java.lang.reflect.Method;

@Aspect
@Component
public class MetricAspect {

    private final MetricsService metricsService;

    public MetricAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("@annotation(ru.kpfu.itis.shakirov.aop.annotation.Metric)")
    public Object measureMetric(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Metric annotation = method.getAnnotation(Metric.class);

        String key = annotation.value().isEmpty()
                ? method.getDeclaringClass().getSimpleName() + "." + method.getName()
                : annotation.value();

        try {
            Object result = joinPoint.proceed();
            metricsService.recordSuccess(key);
            return result;
        } catch (Throwable t) {
            metricsService.recordFailure(key);
            throw t;
        }
    }
}