package ru.kpfu.itis.shakirov.controller;

import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.shakirov.model.MethodMetricEntity;
import ru.kpfu.itis.shakirov.service.MetricsService;
import ru.kpfu.itis.shakirov.service.MetricsService.BenchmarkStats;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final MetricsService metricsService;

    public StatsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public List<MethodMetricEntity> getMetrics() {
        return metricsService.getAllMetrics();
    }

    @GetMapping("/benchmark")
    public Map<String, BenchmarkStats> getAllBenchmarks() {
        return metricsService.getAllBenchmarkStats();
    }

    @GetMapping("/benchmark/percentile")
    public Map<String, Object> getPercentile(
            @RequestParam String method,
            @RequestParam double percentile
    ) {
        long value = metricsService.calculatePercentile(method, percentile);
        return Map.of(
                "method", method,
                "percentile", percentile,
                "valueNs", value,
                "valueMs", value / 1000000.0
        );
    }
}