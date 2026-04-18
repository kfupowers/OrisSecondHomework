package ru.kpfu.itis.shakirov.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.shakirov.aop.annotation.Benchmark;
import ru.kpfu.itis.shakirov.service.MetricsService;

import java.lang.reflect.Method;

@Aspect
@Component
public class BenchmarkAspect {

    private final MetricsService metricsService;

    public BenchmarkAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("@annotation(ru.kpfu.itis.shakirov.aop.annotation.Benchmark)")
    public Object benchmark(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Benchmark annotation = method.getAnnotation(Benchmark.class);

        String key = annotation.value().isEmpty()
                ? method.getDeclaringClass().getSimpleName() + "." + method.getName()
                : annotation.value();

        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.nanoTime() - start;
            metricsService.recordBenchmark(key, duration);
        }
    }
}