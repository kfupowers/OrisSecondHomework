package ru.kpfu.itis.shakirov.aop.stats;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class MetricsStorage {

    private final Map<String, MethodMetric> metrics = new ConcurrentHashMap<>();

    private final Map<String, List<Long>> benchmarkTimes = new ConcurrentHashMap<>();

    public void recordSuccess(String methodKey) {
        metrics.computeIfAbsent(methodKey, k -> new MethodMetric()).incrementSuccess();
    }

    public void recordFailure(String methodKey) {
        metrics.computeIfAbsent(methodKey, k -> new MethodMetric()).incrementFailure();
    }

    public Map<String, MethodMetric> getAllMetrics() {
        return Map.copyOf(metrics);
    }

    public void recordTime(String methodKey, long nanos) {
        benchmarkTimes.computeIfAbsent(methodKey, k -> new CopyOnWriteArrayList<>()).add(nanos);
    }

    public List<Long> getTimesForMethod(String methodKey) {
        return benchmarkTimes.getOrDefault(methodKey, List.of());
    }

    public Map<String, BenchmarkStats> getAllBenchmarkStats() {
        return benchmarkTimes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> BenchmarkStats.fromTimes(e.getValue())
                ));
    }

    public static class MethodMetric {
        private long successCount = 0;
        private long failureCount = 0;

        public synchronized void incrementSuccess() { successCount++; }
        public synchronized void incrementFailure() { failureCount++; }

        public long getSuccessCount() { return successCount; }
        public long getFailureCount() { return failureCount; }
        public long getTotalCount() { return successCount + failureCount; }
    }

    public record BenchmarkStats(long count, long minNs, long maxNs, double avgNs) {
        public static BenchmarkStats fromTimes(List<Long> times) {
            if (times.isEmpty()) {
                return new BenchmarkStats(0, 0, 0, 0.0);
            }
            long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = times.stream().mapToLong(Long::longValue).max().orElse(0);
            double avg = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            return new BenchmarkStats(times.size(), min, max, avg);
        }
    }
}