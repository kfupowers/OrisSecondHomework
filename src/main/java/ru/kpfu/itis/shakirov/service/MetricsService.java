package ru.kpfu.itis.shakirov.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.shakirov.model.BenchmarkRecordEntity;
import ru.kpfu.itis.shakirov.model.MethodMetricEntity;
import ru.kpfu.itis.shakirov.repository.BenchmarkRecordRepository;
import ru.kpfu.itis.shakirov.repository.MethodMetricRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final MethodMetricRepository metricRepository;
    private final BenchmarkRecordRepository benchmarkRepository;

    public MetricsService(MethodMetricRepository metricRepository,
                          BenchmarkRecordRepository benchmarkRepository) {
        this.metricRepository = metricRepository;
        this.benchmarkRepository = benchmarkRepository;
    }

    @Transactional
    public void recordSuccess(String methodKey) {
        int updated = metricRepository.incrementSuccess(methodKey);
        if (updated == 0) {
            MethodMetricEntity entity = new MethodMetricEntity(methodKey);
            entity.setSuccessCount(1);
            metricRepository.save(entity);
        }
    }

    @Transactional
    public void recordFailure(String methodKey) {
        int updated = metricRepository.incrementFailure(methodKey);
        if (updated == 0) {
            MethodMetricEntity entity = new MethodMetricEntity(methodKey);
            entity.setFailureCount(1);
            metricRepository.save(entity);
        }
    }

    public List<MethodMetricEntity> getAllMetrics() {
        return metricRepository.findAll();
    }

    @Transactional
    public void recordBenchmark(String methodKey, long durationNanos) {
        BenchmarkRecordEntity record = new BenchmarkRecordEntity(methodKey, durationNanos);
        benchmarkRepository.save(record);
    }

    public List<BenchmarkRecordEntity> getRecordsForMethod(String methodKey) {
        return benchmarkRepository.findByMethodKey(methodKey);
    }

    public Map<String, BenchmarkStats> getAllBenchmarkStats() {
        List<Object[]> rows = benchmarkRepository.getAggregatedStats();
        Map<String, BenchmarkStats> result = new HashMap<>();
        for (Object[] row : rows) {
            String key = (String) row[0];
            Long count = (Long) row[1];
            Long min = (Long) row[2];
            Long max = (Long) row[3];
            Double avg = (Double) row[4];
            result.put(key, new BenchmarkStats(count, min, max, avg));
        }
        return result;
    }

    public long calculatePercentile(String methodKey, double percentile) {
        List<Long> durations = benchmarkRepository.findAllDurationsSorted(methodKey);
        if (durations.isEmpty()) {
            return 0L;
        }
        int index = (int) Math.ceil(percentile / 100.0 * durations.size()) - 1;
        index = Math.max(0, Math.min(index, durations.size() - 1));
        return durations.get(index);
    }

    public record BenchmarkStats(long count, long minNs, long maxNs, double avgNs) {}
}